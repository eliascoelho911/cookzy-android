# 🧱 Seção 6 — Arquitetura de Componentes (UI)

## 6.1 Visão Geral
- **Stack:** Single-Activity + **Compose Navigation**, **ViewModel** por tela, **State hoisting** e **Unidirectional Data Flow (UDF)**.  
- **Padrões:** `UiState` imutável, `UiEvent` (intents do usuário), `UiEffect` (efeitos one-shot: toast/snackbar/nav).  
- **Escopo:** Home, BookDetail, RecipeDetail (abas), Prep, Import, Nutrition, Search.

---

## 6.2 Rotas & NavGraph
```text
/home
/search?scope={recent|all}&q={query}
/book/{bookId}
/recipe/{recipeId}
/edit/{recipeId?}
/prep/{recipeId}
/import?source={youtube|tiktok|instagram|facebook}
/nutrition/{recipeId}
/converter   (bottom sheet)
/share/{recipeId}   (bottom sheet)
```
- **Deep links**: `/book/{id}?sort=rating&tag=vegan`, `/recipe/{id}#step=3`, `/import?source=youtube`.  
- **Back stack** preserva estado de busca/filtros; **SavedStateHandle** para restauração pós-processo do sistema.

---

## 6.3 ViewModels por Tela (responsabilidades)

### `HomeViewModel`
- Carregar **livros** (grid) e **recentes** (carrossel), lidar com vazio/erro/offline.  
- Emitir efeitos: abrir `/book/{id}`, `/search?scope=recent`.  
- Telemetria: `home_search_used`, `book_opened`.

### `BookDetailViewModel`
- Estado: lista paginada de receitas do livro; **ordenar** (Data↓, Nome A–Z, Avaliação↓), **filtros** (tags/tempo/nutrição).  
- **Busca local embutida** (texto → filtra no client + debounce).  
- Efeitos: abrir `/recipe/{id}`, abrir editor, snackbars.

### `RecipeDetailViewModel`
- Tabs: **Ingredientes / Preparo / Nutrição**; dados normalizados (ingredientes com unidade, passos com timestamps).  
- Integrações: **escalonamento de porções** → recalcula ingredientes e **tabela nutricional**.  
- Efeitos: abrir conversor, abrir vídeo com **timestamp**.

### `PrepViewModel`
- **MiniTimer** com **multi-instância** (passos diferentes).  
- Estados: `idle | running | paused | finished`.  
- Efeitos: vibração/áudio opcional, notificação local, manter ativo ao trocar de tela.

### `ImportViewModel`
- Fontes: **YouTube (MVP)**, depois TikTok/Instagram/Facebook.  
- Fluxo: input URL → **extração** (título/ingredientes/preparo/timestamps) → revisão → salvar receita.  
- Estados de falha: parsing/API indisponível → “Editar manualmente”.

### `NutritionViewModel`
- Calcula tabela (kcal, macro, %VD) **por porção**; **reage ao escalonamento**.  
- Efeito visual: **fade + highlight** nos valores recalculados.

### `SearchViewModel`
- Unifica busca por **título/ingredientes/tags**; escopo `recent | all`.  
- Suporte a **sugestões** e histórico local.

---

## 6.4 Contratos de State / Event / Effect (exemplos Kotlin)

```kotlin
data class HomeUiState(
  val books: List<BookUi> = emptyList(),
  val recent: List<RecipeUi> = emptyList(),
  val isLoading: Boolean = false,
  val error: String? = null,
  val isOffline: Boolean = false
)

sealed interface HomeEvent {
  data object ClickNewBook : HomeEvent
  data class ClickBook(val id: String) : HomeEvent
  data object SeeAllRecent : HomeEvent
  data class SearchChanged(val q: String) : HomeEvent
}

sealed interface HomeEffect {
  data class NavigateTo(val route: String) : HomeEffect
  data class ShowSnackbar(val msg: String) : HomeEffect
}
```

```kotlin
data class BookDetailUiState(
  val bookId: String,
  val query: String = "",
  val sort: Sort = Sort.DateDesc,
  val filters: Filters = Filters(),
  val items: PagedList<RecipeUi> = PagedList.Empty,
  val isLoading: Boolean = false,
  val emptyReason: EmptyReason? = null
)
```

```kotlin
data class PrepUiState(
  val timers: List<StepTimerUi> = emptyList(),
  val activeCount: Int = 0
)
```

---

## 6.5 Data Flow (alto nível)

```
UI (Composable) 
  → ViewModel (Events) 
    → UseCases (domain) 
      → Repository (cache + local + remote)
        → Local: Room/SQLite (livros, receitas, ingredientes, passos)
        → Remote: Import parsers (YouTube API / scraping guardado por feature flag)
  ← ViewModel (UiState/UiEffect)
```

- **Cache-first** para Home/Book; **paging** para listas extensas.  
- **Offline-first** para CRUD; fila de “pendências” para sincronização.  
- **Import**: usa provider específico; se falhar, retorna **formulário pré-preenchido**.

---

## 6.6 Módulos e Reutilização
- `ui-*: home, book, recipe, prep, import, nutrition, search`  
- `design-system`: tema, tokens, componentes base (BookCard, RecipeCard, Tooltip, MiniTimer).  
- `domain`: use cases (ex.: `GetBooks`, `GetRecipesByBook`, `ScaleServings`, `ComputeNutrition`).  
- `data`: repositories, DAOs, mapeadores DTO→domain.  
- `import-providers`: youtube, tiktok… (feature flags).

---

## 6.7 Timers (Multi-instância)
- **Modelo:** `StepTimer(id, label, totalMs, remainingMs, state)`; fonte única (`PrepViewModel`) com **`Ticker` compartilhado**.  
- **Resiliência:** persiste timers ativos (Room) + **WorkManager** para garantir notificação ao término.  
- **UI:** **MiniTimer** compacta lista; expandido mostra controles por item.

---

## 6.8 Escalonamento & Nutrição
- **Escalonamento**: fator `servingsFactor = desired / base`; recalcula quantidades **com arredondamento** por unidade (ex.: colher/gramas).  
- **Nutrição**: serviço/DB local → soma por ingrediente → divide por porção; **rebate efeitos** no `RecipeDetailUiState` e `NutritionUiState` com **transição visual**.

---

## 6.9 Erros, Vazio, Offline (padrões)
- **Erro**: snackbar persistente com ação *tentar novamente*.  
- **Vazio**: ilustração + CTA (“Criar livro”, “Importar do YouTube”).  
- **Offline**: banner “Modo offline — sincroniza depois”; operações CRUD vão para **outbox**.

---

## 6.10 Performance
- **Lazy grids/rows**, **Paging 3**, **coils** de imagem com tamanho fixo e cache.  
- **SnapshotFlow**/`collectAsStateWithLifecycle` para estados.  
- **Stable keys** em listas; **remember** para evitar recomposições.

---

## 6.11 Telemetria (eventos)
- `home_search_used`, `book_opened`, `book_sort_changed`, `recipe_opened`, `timer_started`, `timer_finished`, `import_success`, `nutrition_recalculated`.

---

## 6.12 Segurança & Privacidade (UI)
- Dados locais **apenas no dispositivo** (MVP); opção de **backup** futura.  
- Pedir consentimento para **notificações** (timers) e **telemetria**.

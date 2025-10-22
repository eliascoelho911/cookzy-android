# Cookzy — Front-end Architecture (Android Compose)

## Change Log

| Date       | Version | Description                                | Author     |
|------------|---------|--------------------------------------------|------------|
| 2025-10-22 | 0.1     | Documento criado (esqueleto inicial)       | Sally (UX) |
| 2025-10-22 | 0.2     | Inventário de componentes e padrões de preview | Sally (UX) |

## Visão Geral

Este documento descreve como implementar a UI do Cookzy usando Jetpack Compose, Navigation Compose e Koin, alinhado à Especificação de UI/UX (docs/front-end-spec.md). Foca em padrões de navegação, gerenciamento de estado, injeção de dependências, tema e diretrizes de testes.

## Stack de Front-end

- Linguagem: Kotlin
- UI: Jetpack Compose + Material 3
- Navegação: Navigation Compose com rotas tipadas (kotlinx.serialization)
- DI: Koin
- Concor­rência: Coroutines/Flow
- Persistência: Room (para dados locais)
- Tipografia: Google Fonts Provider (Nunito, Open Sans)
- Deep Links: `cookzy://recipes` (editor/detail)

## Estrutura do Projeto (UI)

- Pacotes principais
  - android: Activity host e bootstrap (MainActivity)
  - ui: `CookzyApp`, `CookzyNavHost` e elementos cross‑cutting
  - ui/theme: `Theme.kt`, `Color.kt`, `Type.kt`
  - navigation: destinos tipados e deep links
  - di: módulos Koin
  - feature/*: telas + ViewModels por funcionalidade
  - core: base de ViewModel e utilitários de estado/efeito

Referências (arquivos atuais):
- app/src/main/java/com/eliascoelho911/cookzy/ui/CookzyApp.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/ui/CookzyNavHost.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/navigation/CookzyDestinations.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/di/AppModules.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/ui/theme/Theme.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/ui/theme/Color.kt:1
- app/src/main/java/com/eliascoelho911/cookzy/ui/theme/Type.kt:1

## Navegação

- Padrão: rotas tipadas usando `@Serializable` + `NavHost`
  - Destinos: `RecipeListDestination`, `RecipeEditorDestination`, `RecipeDetailDestination`
  - Deep links: base `cookzy://recipes`, sufixos `/editor` e `/detail`
- Gráfico de navegação
  - Start: `RecipeListDestination`
  - Transições:
    - Lista → Editor (criar/editar)
    - Lista/Editor/Detalhe → Detalhe com `recipeId`
- Boas práticas
  - Encapsular navegação nos destinos tipados
  - Evitar strings de rota soltas; usar classes de destino
  - Preservar estado na pilha (scroll/aba) nos pops

## Injeção de Dependências (Koin)

- Módulos em `di/AppModules.kt`:
  - databaseModule: `CookzyDatabase` e `recipeDao()`
  - repositoryModule: `OfflineRecipeRepository` → `RecipeRepository`
  - viewModelModule: `RecipeListViewModel`, `RecipeEditorViewModel(params)`, `RecipeDetailViewModel(params)`
- Padrão de uso em telas:
  - `val vm = koinViewModel<FeatureViewModel>(parameters = { parametersOf(arg) })`

## Gerenciamento de Estado

- Base: `BaseViewModel<State, Effect>` com:
  - `StateFlow<State>` para UI state imutável
  - Canal de `Effect` (one‑shot) para navegação/toasts, etc.
  - Helpers: `updateState {}`, `sendEffect(effect)`
- Princípios
  - Um ViewModel por feature/tela
  - UI coleta `state` e `effects` via `collectAsStateWithLifecycle`/`LaunchedEffect`
  - Evitar lógica pesada no Composable; manter no ViewModel

## Composição de UI

- Separar `Route` (injeção/navegação) do `Screen` (UI pura)
- Reutilizar componentes: Cards, Chips, Stepper, Barra de Preparo, Sheets
- Previews obrigatórias por estado relevante (carregando, vazio, erro, sucesso)
- Semântica: fornecer `contentDescription`, roles e ordem de foco previsível

## Tema & Tokens

- `AppTheme` aplica `MaterialTheme` com `AppTypography` e `Color.kt`
- `ExtendedColorScheme` expõe família de cores de sucesso
- Tipografia via Google Fonts Provider; prever fallback quando indisponível
- Respeitar escala de espaçamento base 8dp e shapes consistentes

### Tabela de Tokens (sincronizada com o código)

| Role | Constante (Light) | Hex | Constante (Dark) | Hex |
|---|---|---|---|---|
| Primary | `primaryLight` | #3B6939 | `primaryDark` | #A1D39A |
| On Primary | `onPrimaryLight` | #FFFFFF | `onPrimaryDark` | #0A390F |
| Primary Container | `primaryContainerLight` | #BCF0B4 | `primaryContainerDark` | #235024 |
| On Primary Container | `onPrimaryContainerLight` | #235024 | `onPrimaryContainerDark` | #BCF0B4 |
| Secondary | `secondaryLight` | #775A0B | `secondaryDark` | #E9C16C |
| On Secondary | `onSecondaryLight` | #FFFFFF | `onSecondaryDark` | #3F2E00 |
| Secondary Container | `secondaryContainerLight` | #FFDF9E | `secondaryContainerDark` | #5B4300 |
| On Secondary Container | `onSecondaryContainerLight` | #5B4300 | `onSecondaryContainerDark` | #FFDF9E |
| Tertiary | `tertiaryLight` | #8F4C37 | `tertiaryDark` | #FFB59F |
| On Tertiary | `onTertiaryLight` | #FFFFFF | `onTertiaryDark` | #561F0E |
| Error | `errorLight` | #904A44 | `errorDark` | #FFB4AC |
| On Error | `onErrorLight` | #FFFFFF | `onErrorDark` | #561E1A |
| Background | `backgroundLight` | #F7FBF1 | `backgroundDark` | #10140F |
| On Background | `onBackgroundLight` | #191D17 | `onBackgroundDark` | #E0E4DB |
| Surface | `surfaceLight` | #F7FBF1 | `surfaceDark` | #10140F |
| On Surface | `onSurfaceLight` | #191D17 | `onSurfaceDark` | #E0E4DB |
| Surface Variant | `surfaceVariantLight` | #DEE5D8 | `surfaceVariantDark` | #424940 |
| On Surface Variant | `onSurfaceVariantLight` | #424940 | `onSurfaceVariantDark` | #C2C9BD |
| Outline | `outlineLight` | #72796F | `outlineDark` | #8C9388 |
| Outline Variant | `outlineVariantLight` | #C2C9BD | `outlineVariantDark` | #424940 |
| Success | `successLight` | #39693B | `successDark` | #9FD49C |
| On Success | `onSuccessLight` | #FFFFFF | `onSuccessDark` | #063911 |
| Success Container | `successContainerLight` | #BAF0B6 | `successContainerDark` | #215025 |
| On Success Container | `onSuccessContainerLight` | #215025 | `onSuccessContainerDark` | #BAF0B6 |

Tipografia
- `AppTypography`: usa baseline do Material 3 com famílias definidas em `Type.kt` (Nunito display; Open Sans body/labels).

## Responsividade

- Seguir a seção “Responsividade” da spec (docs/front-end-spec.md)
- Adotar `WindowSizeClass` quando introduzir variações (≥ 840 dp multipainel futuramente)
- Sheets com largura máx. ~640 dp em telas largas

## Acessibilidade

- Target: WCAG 2.2 AA + diretrizes Android
- Regras
  - Alvos de toque ≥ 48dp; foco visível
  - `contentDescription`/`semantics` adequados
  - Suporte a leitor de tela, teclado e preferências de reduzir animações

## Animações & Microinterações

- Usar APIs Compose (AnimatedContent/animate*) e curvas Material
- Respeitar flag de “reduzir animações”; oferecer alternativas (fade/instant)
- Timings sugeridos na spec (tabs, sheets, barra de preparo, stepper)

## Testes

- Unitário ViewModel: MockK + Coroutines Test (`MainDispatcherRule`)
  - Ex.: app/src/test/java/com/eliascoelho911/cookzy/feature/recipeeditor/RecipeEditorViewModelTest.kt:1
- UI (futuro): Compose UI Test + Semantics para estados e navegação
- A11y: Accessibility Scanner manual e validações semânticas em testes

## Performance

- Objetivos: 60 fps; resposta < 100 ms em interações
- Diretrizes
  - Placeholders/skeletons; evitar layout shift (imagens com tamanho conhecido)
  - Chaves estáveis em listas; reduzir recomposições (remember/derivedState)
- Overdraw baixo; evitar sombras excessivas

## Inventário de Componentes (Compose)

Tabela de referência dos principais `@Composable`s a implementar/reutilizar. Servirá para handoff e para organizar prévias e testes.

| Componente | Propósito | Props (chave) | Estados | A11y | Prévias Requeridas |
|---|---|---|---|---|---|
| AppTopBar | Barra superior com título/ações | `title: String`, `onBack: (() -> Unit)?`, `actions: @Composable RowScope.() -> Unit` | padrão, scrolled | botão voltar com label; ordem de foco | padrão; com back; com ações |
| RecentRecipesCarousel | Carrossel de receitas recentes | `items: List<Recipe>`, `onClick(Recipe)`, `peekDp: Dp` | vazio | anunciar “deslize p/ ver mais”; foco previsível | vazio; 1 item; vários |
| BookFilterCarousel | Carrossel de livros (filtro) | `books: List<Book>`, `selected: Book?`, `onSelect(Book?)` | selected/unselected | role=tab/toggle; item “Todos” com label | sem seleção; com seleção |
| ListGridToggle | Alternância lista ↔ grade | `isGrid: Boolean`, `onToggle(Boolean)` | list/grid | role=toggle; descriptions | list; grid |
| RecipeCard | Card de receita em listas | `title`, `time: Duration?`, `livrosDeReceitas: List<String>`, `onClick` | loading, error, vazio | contentDescription no card; foco visível | normal; loading; error |
| BookCard | Card de livro/coleção | `title`, `count: Int`, `onClick` | vazio | leitura por leitor de tela com contagem | normal; vazio |
| PortionStepper | Ajuste de porções | `value: Int`, `onChange(Int)`, `range: IntRange` | min/max, inválido | role=adjustable; announce mudanças | 1, meio, max; fontScale 2.0 |
| PrepBar | Mini‑timer persistente | `title`, `remaining: Duration`, `running: Boolean`, `onToggle()`, `onClose()` | running/paused/finished | announce tempo/restante e estado | running; paused; finished |
| ExternalVideoCTA | Ação “Abrir vídeo externo” | `platform: Platform`, `timestamp: Long`, `onClick` | indisponível | label com plataforma e tempo | disponível; indisponível |
| ConverterSheet | Sheet de conversão de medidas | `onDismiss`, inputs/outputs | erro/validação | foco inicial no primeiro campo | válido; com erro |
| EmptyState | Vazio para listas/telas | `illustration`, `title`, `message`, `primaryAction` | — | elementos com rótulos | Home; Buscar; Livros |
| ErrorState | Exibir erro e ação | `message`, `onRetry` | — | contraste/ícone | genérico; específico |
| TabsRecipeDetail | Abas da receita | `selected: Tab`, `onSelect(Tab)` | com badge | foco por teclado; indicador visível | Ingredientes; Preparo; Nutrição |
| ReorderableList | Lista com arrastar p/ ordenar | `items`, `onMove(from,to)`, `key` | arrastando/solto | alça com hit ≥48dp; ações acessíveis | ingredientes; instruções |
| StepRichText | Renderiza passos com destaques | `text: String`, `entities: StepEntities`, `onIngredientTap(IngredientRef)`, `onTimerTap(Duration)`, `onTempLongPress(Temperature)` | sem entidades | spans com semantics clicáveis | sem; com ingrediente; com timer; com temperatura |
| IngredientTooltip | Tooltip de ingrediente | `anchorRect`, `content: IngredientInfo`, `onConvert()`, `onCopy()`, `onDismiss()` | — | role=dialog; foco inicial | padrão |
| IngredientRow | Linha de ingrediente com destaque | `text: String`, `quantityRange: IntRange?` | sem range | `AnnotatedString` com bold no range | com/sem destaque |
| FilterChips | Chips de filtro/busca | `filters`, `onToggle` | selected/unselected | tamanho ≥48dp | estados selecionado/não |
| CookzyModalBottomSheet | Sheet base com título e ações | `title: String`, `sheetState: SheetState`, `onDismissRequest`, `secondaryButton?`, `primaryButton?`, `content` | com/sem footer | announces sheet; foco e botões acessíveis | aberto; com/sem footer |

Observação: respeitar tokens de `Theme.kt`, `Color.kt`, `Type.kt` e `ExtendedColorScheme`.

### Especificações por Componente (assinaturas sugeridas)

```kotlin
@Composable
fun PortionStepper(
  value: Int,
  onChange: (Int) -> Unit,
  range: IntRange = 1..99,
  modifier: Modifier = Modifier,
) { /* UI + semantics(role = adjustable) */ }

@Composable
fun PrepBar(
  title: String,
  remaining: Duration,
  running: Boolean,
  onToggle: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) { /* slide/fade in/out; evitar layout shift */ }

@Composable
fun ExternalVideoCTA(
  platform: Platform, // YouTube, Instagram, etc.
  timestampSec: Long?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) { /* ícone + label acessível */ }

@Composable
fun StepRichText(
  text: String,
  entities: StepEntities, // ingredientes/tempos/temperaturas com ranges
  onIngredientTap: (IngredientRef) -> Unit,
  onTimerTap: (Duration) -> Unit,
  onTempLongPress: (Temperature) -> Unit,
  modifier: Modifier = Modifier,
) { /* ClickableText + AnnotatedString com spans e ícones inline */ }

@Composable
fun IngredientTooltip(
  content: IngredientInfo, // ex.: quantidade + nome
  onConvert: () -> Unit,
  onCopy: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) { /* Popup/DropdownMenu estilizado; role=dialog; foco inicial */ }

@Composable
fun IngredientRow(
  text: String,
  quantityRange: IntRange?,
  modifier: Modifier = Modifier,
) { /* buildAnnotatedString { addStyle(FontWeight.Bold, range) } */ }
```

### Estados & Semântica
- PortionStepper: anunciar novo valor; teclas setas/volume ajustam; limites com feedback háptico leve.
- PrepBar: ícone play/pause com `stateDescription`; tempo restante legível por leitor de tela.
- ExternalVideoCTA: rótulo inclui plataforma e timestamp normalizado.

## Padrões de Preview (Compose)

Objetivo: garantir que cada componente/tela tenha prévias úteis para revisão rápida, QA e desenvolvimento acelerado.

### Convenções
- Nome: `Preview<Component>_<State>` (ex.: `PreviewPortionStepper_Min`, `PreviewPrepBar_Running`).
- Tema: envolver sempre em `AppTheme(darkTheme = false/true)`.
- Fontes: adicionar variação com `uiMode = Configuration.UI_MODE_NIGHT_YES` e `fontScale` (quando aplicável via parâmetro em prévia customizada).
- Tamanhos: pelo menos um device phone (411x891 dp) e uma variação ≥ 840 dp para telas largas quando aplicável.
- A11y: criar prévia com `fontScale` ~2.0 para validar quebras/overflow.

### MultiPreview utilitário

```kotlin
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreviews

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
  AppTheme { content() }
}
```

Usar `@ThemePreviews` + `PreviewWrapper { ... }` nos previews de componentes.

### Prévias por Componente (mínimo)
- PortionStepper: min, middle, max; fontScale 2.0.
- PrepBar: running, paused, finished.
- RecipeCard: normal, loading, error.
- TabsRecipeDetail: cada aba selecionada.
- EmptyState: Home/Buscar/Livros.
- ConverterSheet: válido e com erro de validação (campo vazio/inválido).
- StepRichText: sem entidades; com ingrediente; com timer; com temperatura.
- IngredientTooltip: padrão.
- IngredientRow: com/sem destaque da quantidade; variação com fontScale 2.0.

### Prévias de Telas
- Lista de Receitas: vazio; com itens; erro.
  - Arquivo: app/src/main/java/com/eliascoelho911/cookzy/feature/recipelist/RecipeListScreen.kt
- Detalhe da Receita: carregando; sucesso; erro.
  - Arquivo: app/src/main/java/com/eliascoelho911/cookzy/feature/recipedetail/RecipeDetailScreen.kt
- Editor de Receita: novo; edição existente; validação com erros.
  - Arquivo: app/src/main/java/com/eliascoelho911/cookzy/feature/recipeeditor/RecipeEditorScreen.kt

### Dados de Preview
- Criar `PreviewData.kt` em cada feature com fábricas estáveis de modelos (ex.: `sampleRecipe()`).
- Considerar `@PreviewParameter` para gerar casos comuns (quando útil).

## Pontos em Aberto

- Política de múltiplos timers (um ativo por vez vs paralelos)
- Mini‑timer persistente: escopo de vida e comportamento ao fechar
- CTA “Abrir vídeo externo”: fallback quando app externo indisponível

## Referências

- Especificação de UI/UX: docs/front-end-spec.md
- PRD: docs/prd.md

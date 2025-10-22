# Livros de Receitas — Detalhe — Wireframe (MVP)

Fonte: docs/epics/02-livros-de-receitas.md • docs/front-end-spec.md (IA/Navegação/Estados) • docs/ui-architecture.md (BookCard, RecipeCard, Empty/Error).

Objetivo
- Exibir as receitas associadas a um livro, permitir adicionar/remover receitas deste livro e gerenciar ações do livro (renomear/excluir).

Elementos‑chave
- App Bar reutilizada da Home com voltar, título do livro, MODO DE BUSCA opcional embutido na App Bar (campo de busca inline) e menu (⋮) com Renomear • Excluir.
- Cabeçalho do livro: capa/cor/placeholder, título, contagem de receitas.
- CTA principal “Adicionar receitas a este livro”.
- Lista de receitas (cards padrão); por item, overflow (⋮) com “Remover do livro”.
- Estados: vazio/erro/carregando (skeleton).

Wireframe (Mobile)

```
┌──────────────── App Bar ───────────────────────────────────────────┐
│ ←  Título do Livro   [ 🔎  Filtrar receitas…            ( ✕ ) ]  (⋮) │
│                               ↑ busca inline (opcional)             │
└────────────────────────────────────────────────────────────────────┘

┌────────────── Cabeçalho ────────────────┐
│  ○  Título do Livro                      │  ○ capa/cor/placeholder
│     12 receitas                          │
└──────────────────────────────────────────┘

[  Adicionar receitas a este livro  ]      ← boto primário

  (busca movida para a App Bar)

Lista de receitas (LazyColumn)
┌────────────── Card Receita ─────────────┐
│ [img]  Título da Receita            (⋮) │  ← Remover do livro
│        ⏱ 25 min  •  [Livro X]          │
└─────────────────────────────────────────┘
┌────────────── Card Receita ─────────────┐
│ [img]  …                             (⋮) │
└─────────────────────────────────────────┘
```

Fluxo — Adicionar receitas (seleção múltipla)

```
┌────────────────── Selecionar receitas ──────────────────┐  ← rota dedicada (tela cheia)
│ ←  Selecionar receitas                         [  OK ]  │  OK habilita com ≥1 seleção
├─────────────────────────────────────────────────────────┤
│ [ 🔎  Buscar receitas…                        ( ✕ ) ]   │
├─────────────────────────────────────────────────────────┤
│ [☐]  Título da Receita A       ⏱ 25 min                │
│ [☑]  Título da Receita B       ⏱ 15 min                │
│ [☐]  …                                                  │
├─────────────────────────────────────────────────────────┤
│  [ Cancelar ]                               [  Adicionar ]  │
└─────────────────────────────────────────────────────────┘
```

Estados
- Vazio (detalhe): ilustração + mensagem “Este livro ainda não tem receitas” + CTA [Adicionar receitas].
- Carregando: skeleton de cards.
- Erro: ErrorState com mensagem + [Tentar de novo]. Em remover/adicionar, snackbar de erro com ação repetir.

Interações
- Menu (⋮) da App Bar → Renomear (dialog) • Excluir (confirmar; se houver receitas, confirmar novamente). Após excluir, navegar de volta com snackbar “Livro excluído”.
- “Adicionar receitas” → abre tela de Selecionar receitas; permite buscar e marcar múltiplas; confirmar adiciona todas e retorna com snackbar “N receitas adicionadas”.
- Remover do livro (⋮) no card → remove imediatamente com snackbar “Removida de ‘Livro’” + [Desfazer].
- Busca (na App Bar): filtro textual local (case‑insensitive) sobre receitas deste livro; limpar (✕) restabelece a lista completa.

Acessibilidade
- Cabeçalho com roles de título/heading; contagem anunciada.
- Botões e menus com labels claros; `contentDescription` em (⋮) do card “Mais opções da receita”.
- Campo de busca NA APP BAR com label/placeholder claros; `imeAction = Search`; foco e leitura apropriados.
- Tela de seleção: checkboxes com rótulos completos (título + tempo); foco previsível; `imeAction = Search`.
- Snackbars anunciam o resultado (“3 receitas adicionadas”).

Responsividade
- Mobile (MVP): 1 coluna.
- ≥ 840 dp (futuro): grid 2–3 colunas no detalhe opcional; fora de escopo.

Notas para Dev
- Arquivo base sugerido: app/src/main/java/.../bookdetail/BookDetailScreen.kt:1
- Estado:
  - `book: Book`, `recipes: List<Recipe>`, `uiState: Empty|Loading|Error|Success`.
  - Seleção: `SelectRecipesRoute` com `query`, `candidates: List<Recipe>`, `selected: Set<Id>`.
- UI/Comportamento:
  - App Bar: reutilizar `AppTopBar` com `search: SearchUi?` (busca inline opcional) e menu (⋮) para renomear/excluir.
  - Cabeçalho: BookCard grande (não clicável) com capa/placeholder, título e contagem.
  - CTA: `Button` primário acima da lista; em vazio, usar EmptyState com mesmo CTA.
  - Lista: `RecipeCard` padrão; overflow por item com “Remover do livro”.
  - Selecionar receitas: usar tela dedicada (melhor para listas longas) com checkboxes, busca e ação primária “Adicionar”.
- Prévias: detalhe vazio/itens, seleção com 0/3 selecionados, tema claro/escuro, fontScale 2.0.

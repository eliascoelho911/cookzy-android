# Livros de Receitas — Lista — Wireframe (MVP)

Fonte: docs/epics/02-livros-de-receitas.md • docs/front-end-spec.md (IA/Navegação/Estados) • docs/ui-architecture.md (BookCard, Empty/Error).

Objetivo
- Permitir ao usuário visualizar, buscar e gerenciar livros de receitas (coleções): criar, renomear, excluir e navegar para o detalhe do livro.

Elementos‑chave
- App Bar reutilizada da Home com título “Livros de Receitas” e MODO DE BUSCA opcional embutido na própria App Bar (campo de busca inline). Sem ação de criar na App Bar — criação somente via FAB.
- Lista de livros (um por linha) exibindo capa/cor/placeholder circular, título e contagem de receitas.
- Overflow por item (⋮) com ações: Renomear • Excluir.
- FAB “Novo livro”.
- Estados: vazio/erro/carregando (skeleton).

Wireframe (Mobile)

```
┌──────────────── App Bar ───────────────────────────────────────────┐
│ ←  Livros de Receitas   [ 🔎  Buscar por título…        ( ✕ ) ]   │
│                               ↑ busca inline (opcional)            │
└───────────────────────────────────────────────────────────────────┘

  Lista (LazyColumn)
┌────────────── Book (linha) ─────────────┐
│  ○  Título do Livro                 (⋮) │  ○ capa/cor/placeholder
│     12 receitas                        │
└────────────────────────────────────────┘
┌────────────── Book (linha) ─────────────┐
│  ○  …                                (⋮) │
│     0 receitas                         │
└────────────────────────────────────────┘

                       ⊕                    ← FAB: “Novo livro”
```

Estados
- Vazio: ilustração + mensagem “Você ainda não tem livros” + CTA [Criar primeiro livro].
- Carregando: skeleton de 6–8 linhas (círculo + barras de texto).
- Erro: ErrorState com mensagem e ação [Tentar de novo].

Interações
- FAB (único ponto de criação) → abre diálogo “Novo livro” com TextField (título) e ações Cancelar/Criar. Desabilitar Criar quando vazio/ inválido (trim).
- Tap em item → navega para Detalhe do Livro.
- (⋮) por item → Renomear (abre diálogo com TextField, confirma renomeio) • Excluir (confirmação com contagem de receitas; se > 0, requer confirmação extra). Snackbar “Livro excluído” com ação Desfazer (se suportado).
- Busca (na App Bar): aplica debounce (300–500 ms); filtro case‑insensitive por título; limpar (✕) restabelece lista completa.

Acessibilidade
- Alvos ≥ 48dp; linha inteira do item clicável; (⋮) com contentDescription “Mais opções do livro”.
- Leitura do item: “Título do Livro, 12 receitas”.
- Campo de busca NA APP BAR com label/placeholder claros; foco visível; `imeAction = Search`.
- Diálogo: foco inicial no campo; botões rotulados; anunciar erros de validação.

Responsividade
- Mobile (MVP): 1 coluna, lista vertical.
- ≥ 840 dp (futuro): pode alternar para grid de cartões (2–3 col), fora do escopo.

Notas para Dev
- Arquivo base sugerido: app/src/main/java/.../books/BooksListScreen.kt:1
- Estado:
  - `query: String`, `books: List<Book>`, `uiState: Empty|Loading|Error|Success`.
  - Eventos: `onCreateBook(title)`, `onRenameBook(id, title)`, `onDeleteBook(id)`, `onOpenBook(id)`.
- UI:
  - AppTopBar reutilizada (Home) com `search: SearchUi?` para busca inline; criação de livro somente via FAB (sem [＋] na App Bar).
  - BookCard (linha): capa circular (placeholder gerado por hash/cor), título, contagem; overflow (⋮) com Menu.
  - EmptyState/ErrorState reutilizáveis (docs/ui-architecture.md).
  - Dialogs: `AlertDialog` para criar/renomear/excluir; validação inline; `TextField` com `singleLine`.
- A11y/Preview:
  - Prévias: vazio, carregando, erro, com itens (0 e 12 receitas), fontScale 2.0, tema claro/escuro.

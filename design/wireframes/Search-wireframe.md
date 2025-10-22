# Search — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Buscar) • docs/epics/03-busca-e-filtros.md • docs/ui-architecture.md (Empty/Error/FilterChips).

Objetivo
- Permitir localizar receitas rapidamente por texto e filtros básicos, com feedback claro para estados vazio/erro/carregando.

Elementos‑chave
- App Bar com voltar e campo de busca focável (ícone [🔍], limpar [✕]).
- Campo de busca com debounce na digitação.
- Chips de filtro (combináveis) — livro(s) de receitas, tempo (ex.: < 15 min, 15–30, > 30), opcionalmente ingredientes.
- Cabeçalho com contagem “N resultados” após a primeira resposta.
- Lista de resultados em lista padrão (card com cover, título, tempo, livros de receitas).
- Estados: vazio (ilustração + CTA “Tentar outra busca”), carregando (skeleton), erro (mensagem + “Tentar de novo”).

Wireframe (Mobile)

```
┌────────────────── App Bar (Search) ──────────────────┐
│ ←  [ 🔍  Buscar receitas…                (   ✕   ) ] │  ← TextField (foco ao entrar)
└──────────────────────────────────────────────────────┘

  Filtros
┌──────────────── Chips (FilterChips) ────────────────┐
│ [ Todos os livros ] [ Livro A ] [ Livro B ]  [ + ]  │  ← múltipla seleção
│ [  <15m ] [ 15–30m ] [ >30m ]                      │  ← tempo (exclusivos entre si)
└─────────────────────────────────────────────────────┘

  Cabeçalho
┌─────────────────────────────────────────────────────┐
│  18 resultados                                      │
└─────────────────────────────────────────────────────┘

  Lista (LazyColumn)
┌────────────── Card Receita ─────────────┐
│ [img]  Título com destaque da query     │  ← ex.: “Bolo de Cenoura” (negrito na query)
│        ⏱ 25 min  •  [Livro X]          │
└─────────────────────────────────────────┘
┌────────────── Card Receita ─────────────┐
│ [img]  …                                │
└─────────────────────────────────────────┘

— Estados —
• Sem consulta (inicial):
  ┌────────────── EmptyState: Buscar ──────────────┐
  │  Ilustração                                    │
  │  Busque suas receitas                          │
  │  Digite um termo acima para começar            │
  └────────────────────────────────────────────────┘

• Sem resultados (consulta feita):
  ┌────────────── EmptyState: 0 resultados ────────┐
  │  Não encontramos nada para “bolo cenora”       │
  │  Dica: verifique a ortografia ou remova filtros│
  │  [ Tentar outra busca ]                        │
  └────────────────────────────────────────────────┘

• Carregando: skeleton de 3–5 cards.

• Erro: ErrorState com mensagem + [Tentar de novo].
```

Interações
- Entrar na tela: foca o campo; teclado aberto. Voltar (←) fecha o teclado e sai.
- Digitação: aplica debounce (ex.: 300–500 ms). Limpar [✕] limpa consulta e volta ao estado “Sem consulta”.
- Chips de filtro: toques alternam seleção; tempo é mutuamente exclusivo; livros podem ser múltiplos. Atualizam a query corrente e disparam nova busca.
- Resultado: tap no card → navega para Detalhe da Receita.
- Erro: “Tentar de novo” repete a última busca com mesmos filtros.

Acessibilidade
- Campo de busca com label/placeholder claros e `imeAction = Search`; anunciar resultados (“18 resultados”).
- Chips com role=toggle (ou radio para tempo); `contentDescription` com estado selecionado.
- Cards com `contentDescription` sintetizada: título + tempo + primeiro livro.
- Empty/Error com títulos e botões rotulados; foco vai ao campo ao limpar.

Responsividade
- Mobile (MVP): tela única. Em ≥ 840 dp (futuro), painel lateral para filtros — fora de escopo.

Notas para Dev
- Arquivo base: app/src/main/java/.../search/SearchScreen.kt
- Estado:
  - `query: String`, `filters: SearchFilters`, `results: PagingData<Recipe>` ou lista simples no MVP.
  - `uiState: Empty | Loading | Error(msg) | Success(count)`.
- UI:
  - App Bar com `TextField` (singleLine, trailingIcon = limpar; leadingIcon = lupa).
  - `FilterChips` (docs/ui-architecture.md) para livros/tempo.
  - `LazyColumn` para resultados; destacar trechos da query no título via `AnnotatedString` (bold).
  - `EmptyState`/`ErrorState` reutilizados (docs/ui-architecture.md).
  - Skeletons: placeholders de cards (3–5) durante carregamento.
- Lógica:
  - Debounce em digitação; cancelar busca anterior ao iniciar nova; surface de erros de rede.
  - Filtro de tempo traduz para ranges de duração; filtros de livros enviam IDs selecionados.
- Prévias: vazio (inicial), carregando, erro, 0 resultados, resultados.

Escopo (MVP)
- Em escopo: busca textual por título/ingredientes/livros; filtros de tempo e livros combináveis; navegação para Detalhe.
- Fora de escopo: histórico de buscas, sugestões inteligentes/ML, ordenação avançada.


# Recipe List — Wireframe (MVP)

Fonte: docs/front-end-spec.md (IA, Navegação, Wireframes & Mockups) e docs/ui-architecture.md (Prévias de Telas).

Objetivo
- Descoberta rápida (recentes) e acesso a livros; ponto de entrada para criar (Importar/Manual).

Decisão desta revisão
- Inspirado no layout do anexo, o topo da tela agora traz um carrossel horizontal “Receitas recentes” exibindo UMA receita completa por vez (full‑bleed de um card por viewport, com snap). A spec já foi alinhada para refletir 1 por viewport no MVP.

Elementos‑chave
- App Bar com título e ícone de busca.
- Carrossel horizontal “Receitas recentes” (1 card por viewport; snapping; com PEEK do próximo item — sem indicador de página).
- Carrossel horizontal “Livros de receitas” (filtro) com capas circulares + rótulo; seleciona um livro e filtra a lista abaixo; inclui opção “Todos”.
- Lista de receitas com cabeçalho “N receitas” e ações para alternar layout (lista ↔ grade).
- Empty/Erro/Skeleton coerentes para carrosséis e lista.
- FAB “Adicionar receita” → abre sheet com opções (Importar/Manual).

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│  Receitas                        [🔍]   │  ← ícone/ação de busca
└─────────────────────────────────────────┘

  Recentes
┌───────────────── Carrossel (HorizontalPager) ─────────────────┐
│ ┌────────────── Card Receita (XL) ──────────────────────────┐ │
│ │ [cover 16:9]                                             │ │
│ │ Título da Receita                                        │ │
│ │ 25 min  •  [Livro X]                                     │ │
│ └───────────────────────────────────────────────────────────┘ │
│        ⟵ deslize → (snap 1 por vez; com PEEK do próximo)      │
└────────────────────────────────────────────────────────────────┘

  Livros (filtro)
┌────────────── Carrossel de Livros (LazyRow) ────────────────┐
│  (Todos)  (Capa○ + Título)  (Capa○ + Título)  (Capa○ + ...) │
│   ● selecionado  ○ não selecionado                           │
└──────────────────────────────────────────────────────────────┘

  Cabeçalho da lista
┌──────────────────────────────────────────────────────────────┐
│  172 receitas                      [≣↔]  [▦]                │
│                                     lista   grade            │
└──────────────────────────────────────────────────────────────┘

  Lista/Grade de receitas (conforme toggle)
┌────────────── Card Receita ─────────────┐    ┌─────────────┐
│ [img]  Título da Receita                │    │   [img]     │
│        25 min  •  [Livro X]            │    │   Título    │
└─────────────────────────────────────────┘    │   25 min    │
                                               │  [Livro X]  │
                                               └─────────────┘

                      ⊕                        ← FAB (Adicionar receita)
```

Estados
- Carrossel Recentes vazio: ocultar seção OU exibir placeholder “Sem recentes” + CTA “Criar/Importar receita”.
- Carrossel Livros vazio: mostrar item “Todos” apenas; grid/lista mostra todas as receitas.
- Erro: mensagens claras com ação “Tentar de novo”.
- Carregando: skeleton do card XL no carrossel; skeleton circular nos livros; placeholders na lista/grade.

Interações
- Carrossel Recentes: swipe horizontal com snapping por cartão; tap no card → Detalhe da Receita; PEEK comunica continuidade (sem indicador de página).
- Carrossel Livros (filtro): tap seleciona livro e atualiza lista/grade abaixo; item “Todos” limpa filtro.
- Cabeçalho da lista: mostra contagem “N receitas” após filtros; botões alternam layout lista ↔ grade e persistem preferência local.
- Lista/Grade: tap no card → Detalhe.
- FAB → sheet “Adicionar receita” (Importar/Manual). Alvos ≥ 48dp.
- Buscar: via ícone na App Bar (campo inline/rota dedicada, conforme spec).

Acessibilidade
- Carrossel Recentes: leitura do card inclui título, tempo e livros; anunciar “deslize para ver mais”.
- Carrossel Livros: itens com role=tab ou toggle button; label do livro; estado selecionado anunciado; item “Todos” com descrição “mostrar todas as receitas”.
- Cabeçalho: botões de alternância com roles adequados e `contentDescription` (“Exibir como lista”, “Exibir como grade”).
- Ordem de foco natural (App Bar → Recentes → Livros/filtro → Cabeçalho/contagem → Lista/grade → FAB); contraste conforme tema.

Responsividade
- Mobile (MVP): 1 card por viewport no carrossel de recentes (com PEEK); carrossel de livros mostra 4–6 itens visíveis conforme largura; lista padrão (1 col) e grade 2 colunas.
- ≥ 840 dp (futuro): carrossel de recentes pode aumentar a largura do PEEK; carrossel de livros mostra mais itens; grade 3–4 colunas.

Notas para Dev
- Arquivo base: app/src/main/java/.../recipelist/RecipeListScreen.kt
- Recentes: `HorizontalPager` (pageSize = Fill, contentPadding para PEEK; pageSpacing leve; snap). Sem indicador; considerar sombreado suave no PEEK.
- Livros (filtro): `LazyRow` com itens circulares (capa + label) como `FilterChip`/`AssistChip` selecionáveis; incluir item “Todos”.
- Cabeçalho: “N receitas” deriva da lista filtrada; toggles com `IconToggleButton` ou dois `IconButton`s mutuamente exclusivos (persistir em `DataStore`).
- Lista/Grade: `LazyColumn` cards padrão; `LazyVerticalGrid` 2 col; manter chaves estáveis; placeholders/skeletons.
- Prévias: recentes vazio/itens; livros vazio/itens; lista em lista e em grade; erro.

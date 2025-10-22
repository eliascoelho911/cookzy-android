# Recipe List — Wireframe (MVP)

Fonte: docs/front-end-spec.md (seções IA, Navegação, Wireframes & Mockups) e docs/ui-architecture.md (Prévias de Telas).

Objetivo
- Descobrir e navegar receitas rapidamente; ponto de entrada para criar (Importar/Manual).

Elementos‑chave
- App Bar com título e ícone de busca.
- Lista vertical de receitas (cards com título, tempo, tags).
- Empty/Erro/Skeleton.
- FAB “Adicionar receita” → abre sheet com opções (Importar/Manual).

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│  Receitas                        [🔍]   │  ← ícone/ação de busca
└─────────────────────────────────────────┘

┌────────────── Card Receita ─────────────┐
│ [img/cover]  Título da Receita          │
│              25 min   •  #veg  #rápida  │
└─────────────────────────────────────────┘
┌────────────── Card Receita ─────────────┐
│ [img/cover]  ...                        │
└─────────────────────────────────────────┘

   (Estados: vazio/erro/skeleton conforme abaixo)

                         ⊕                 ← FAB (Adicionar receita)
```

Estados
- Vazio: ilustração + título + CTA “Criar primeira receita”.
- Erro: mensagem + ação “Tentar de novo”.
- Carregando: skeleton dos cards.

Interações
- Tap em card → Detalhe da Receita.
- FAB → sheet “Adicionar receita” (Importar/Manual). 48dp touch targets.
- Buscar: via ícone na App Bar (campo inline/rota dedicada, conforme spec).

Acessibilidade
- contentDescription no card (título + tempo + tags).
- Foco/ordem natural; alvos ≥ 48dp; contraste conforme tema.

Responsividade
- MVP 1 coluna (phone). Futuro (≥ 840 dp): 2–3 colunas opcionais (fora do escopo aqui).

Notas para Dev
- Arquivo base: app/src/main/java/.../recipelist/RecipeListScreen.kt
- Adicionar preview para: vazio; com itens; erro.


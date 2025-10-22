# Cookzy — Wireframes (MVP)

Este diretório contém wireframes de baixa fidelidade para as principais telas do MVP, derivados da Especificação de UI/UX (docs/front-end-spec.md) e alinhados à arquitetura (docs/ui-architecture.md).

- Recipe List: design/wireframes/RecipeList-wireframe.md
- Recipe Detail: design/wireframes/RecipeDetail-wireframe.md
- Recipe Editor: design/wireframes/RecipeEditor-wireframe.md
- Search: design/wireframes/Search-wireframe.md
- FAB Sheet: design/wireframes/FabSheet-wireframe.md
- Importar: design/wireframes/Import-wireframe.md
- Livros de Receitas — Lista: design/wireframes/BooksList-wireframe.md
- Livros de Receitas — Detalhe: design/wireframes/BookDetail-wireframe.md
- Prep Bar (mini‑timer): design/wireframes/PrepBar-wireframe.md
 - Preparo (Tela focada): design/wireframes/PrepScreen-wireframe.md

Wireframes pendentes
- Compartilhar (sheet): acionado no Detalhe, payload/preview — base em docs/epics/13-compartilhamento.md

Observações gerais
- Foco mobile (phone); manter 1 coluna no MVP. Em ≥ 840 dp, futuro multipainel está fora de escopo.
- Acessibilidade: alvos ≥ 48dp; ordem de foco previsível; rótulos e roles semânticos.
- Sheets (Compartilhar, FabSheet, Nutrição) devem usar o componente base `CookzyModalBottomSheet` (header com título + IconButton fechar, corpo dinâmico e até dois botões no footer em coluna, full‑width). Interações e comportamentos seguem docs/front-end-spec.md.

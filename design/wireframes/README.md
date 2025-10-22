# Cookzy — Wireframes (MVP)

Este diretório contém wireframes de baixa fidelidade para as principais telas do MVP, derivados da Especificação de UI/UX (docs/front-end-spec.md) e alinhados à arquitetura (docs/ui-architecture.md).

- Recipe List: design/wireframes/RecipeList-wireframe.md
- Recipe Detail: design/wireframes/RecipeDetail-wireframe.md
- Recipe Editor: design/wireframes/RecipeEditor-wireframe.md
- Search: design/wireframes/Search-wireframe.md
- FAB Sheet: design/wireframes/FabSheet-wireframe.md

Wireframes pendentes
- Livros de Receitas: lista e detalhe do livro + associação/desassociação — base em docs/epics/02-livros-de-receitas.md
- Conversor de Medidas (bottom sheet): campos, validações e erros — base em docs/epics/10-conversor-de-medidas.md e front-end-spec (Conversor)
- Fluxo de Importação: origem/link → extrair → revisar → salvar (estados de loading/erro) — base em docs/front-end-spec.md e docs/epics/04-importacao-de-receitas.md
- Compartilhar (sheet): acionado no Detalhe, payload/preview — base em docs/epics/13-compartilhamento.md
- Prep Bar (mini‑timer): estados rodando/pausado/concluído e regras de exibição persistente — base em docs/front-end-spec.md

Observações gerais
- Foco mobile (phone); manter 1 coluna no MVP. Em ≥ 840 dp, futuro multipainel está fora de escopo.
- Acessibilidade: alvos ≥ 48dp; ordem de foco previsível; rótulos e roles semânticos.
- Sheets (Conversor, Compartilhar, FabSheet, Nutrição) devem usar o componente base `CookzyModalBottomSheet` (header com título + IconButton fechar, corpo dinâmico e até dois botões no footer). Interações e comportamentos seguem docs/front-end-spec.md.

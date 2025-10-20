#+ Epic 02 — Livros de Receitas (Coleções)

## Objetivo
Organizar receitas em coleções temáticas com criação, renomeio, exclusão e associação de receitas.

## Contexto
Amplia a descoberta e organização pessoal, melhora a navegação e o retorno às receitas salvas.

## Escopo
- Em escopo: criar/renomear/excluir livros; adicionar/remover receitas; listagem e detalhe do livro.
- Fora de escopo (MVP): compartilhamento de livros completos; ordenação avançada customizada por usuário.

## Requisitos Funcionais
- CRUD de livros de receitas.
- Associar/desassociar receitas a um livro no detalhe da receita e no detalhe do livro.
- Lista de livros com contagem de receitas e busca textual simples.

## Critérios de Aceitação (alto nível)
- [ ] Usuário cria, renomeia e exclui livros com feedback claro.
- [ ] Usuário adiciona/remove receita a um livro de modo confiável.
- [ ] Persistência local consistente com o Epic 01.

## Dependências
- Epic 01 (Receitas) para vínculo entre entidades.

## Riscos
- Relações N:N exigem atenção a migração/esquema.

## Métricas de Sucesso
- Aumento de reuso e acesso rápido; contribui para retenção ≥ 80%.

## Stories sugeridas
1. Esquema de Livro e tabela de junção Livro-Receita.
2. Lista e Detalhe do Livro com contagem.
3. Ação de adicionar/remover receita a partir do Detalhe da Receita.


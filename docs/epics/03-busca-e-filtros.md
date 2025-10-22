#+ Epic 03 — Busca e Filtros

## Objetivo
Encontrar receitas rapidamente por título, ingredientes e livros de receitas com resultados navegáveis.

## Contexto
Fundamental para descoberta e retorno às receitas; acelera fluxo Home → Busca → Detalhe.

## Escopo
- Em escopo: busca textual; filtros por livros de receitas/ingredientes; resultados com navegação para Detalhe.
- Fora de escopo (MVP): busca semântica/ML; histórico de buscas.

## Requisitos Funcionais
- Campo de busca com debounce e listagem de resultados.
- Filtros básicos combináveis (E/OU) conforme design acordado.
- Destaque de termos no resultado e empty state.

## Critérios de Aceitação (alto nível)
- [ ] Busca por título, livros de receitas e ingredientes retorna itens corretos.
- [ ] Filtros ajustam resultados em tempo hábil.
- [ ] Navegação para Detalhe consistente.

## Dependências
- Esquema de dados do Epic 01; índices locais para performance.

## Riscos
- Consultas lentas em dispositivos modestos; mitigar com índices/limites.

## Métricas de Sucesso
- Uso frequente da busca correlaciona com retenção ≥ 80%.

## Stories sugeridas
1. Índices e consultas (Room) para título/livros de receitas/ingredientes.
2. UI/UX de busca com estados (vazio/erro/loading).
3. Filtros base e navegação para Detalhe.

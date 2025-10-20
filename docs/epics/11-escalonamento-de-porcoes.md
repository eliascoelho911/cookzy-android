#+ Epic 11 — Escalonamento de Porções

## Objetivo
Ajustar quantidades dos ingredientes conforme o número de porções, atualizando também a tabela nutricional.

## Contexto
Função central em preparo e planejamento; integra com menções interativas e nutrição.

## Escopo
- Em escopo: controle de porções; recálculo de ingredientes; propagação para nutrição e tooltips.
- Fora de escopo (MVP): arredondamentos customizáveis por ingrediente.

## Requisitos Funcionais
- Controle (stepper/slider/input) para alterar porções.
- Atualização reativa das quantidades e valores nutricionais.

## Critérios de Aceitação
- [ ] Alterar porções reflete imediatamente em ingredientes, tooltips e nutrição.
- [ ] Estado persiste ao navegar dentro do Detalhe.

## Dependências
- Epic 12 (Tabela Nutricional), Epic 08 (Menções), Epic 01 (modelo de ingredientes).

## Riscos
- Erros de arredondamento; mitigar com regras claras e testes.

## Métricas de Sucesso
- Meta PRD: ≥ 70% das receitas criadas com porções ajustadas.

## Stories sugeridas
1. Estado de porções por receita + recálculo de ingredientes.
2. Integração com Nutrição e Tooltips.


#+ Epic 08 — Menções de Ingredientes Interativas

## Objetivo
Ao tocar em ingrediente citado no passo, exibir tooltip com nome, quantidade e observações.

## Contexto
Conecta texto do preparo aos dados estruturados de ingredientes; integra com escalonamento.

## Escopo
- Em escopo: reconhecimento de menções; tooltip contextual; link com quantidade atual (após escalonamento).
- Fora de escopo (MVP): NLP avançado para ambiguidades complexas.

## Requisitos Funcionais
- Resolver referência de menção → item na lista de ingredientes.
- Exibir tooltip com dados e fechar por toque fora.

## Critérios de Aceitação
- [ ] Menções válidas mostram tooltip com dados corretos.
- [ ] Escalonamento reflete valores atualizados no tooltip.

## Dependências
- Epic 11 (Escalonamento de Porções), Epic 01 (modelo de ingredientes).

## Riscos
- Ambiguidades de texto; mitigar com heurísticas simples e edição manual.

## Métricas de Sucesso
- Reduz erros durante preparo; melhora fluidez.

## Stories sugeridas
1. Parser de menções e resolução simples para ingredientes.
2. Componente de tooltip integrado ao fluxo de preparo.


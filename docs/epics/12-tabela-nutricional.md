#+ Epic 12 — Tabela Nutricional

## Objetivo
Mostrar calorias, macronutrientes e micronutrientes principais, ajustáveis por porção.

## Contexto
Entrega valor para planejamento alimentar; integra com escalonamento.

## Escopo
- Em escopo: cálculo/lookup de macro/micronutrientes básicos; exibição por porção.
- Fora de escopo (MVP): bancos extensos/integrações pagas; planos alimentares.

## Requisitos Funcionais
- Calcular/agregar nutrientes com base nos ingredientes e porções.
- UI clara com totais e por porção.

## Critérios de Aceitação
- [ ] Valores ajustam corretamente ao mudar porções.
- [ ] Fallback para ingredientes sem dados com indicação ao usuário.

## Dependências
- Fonte de dados nutricionais; Epic 11 (porções); Epic 01 (ingredientes).

## Riscos
- Confiabilidade da base de dados; mitigar com fonte conhecida e marcações de incerteza.

## Métricas de Sucesso
- Suporte ao objetivo de experiência personalizada e informada.

## Stories sugeridas
1. Modelo/núcleo de cálculo nutricional + testes.
2. UI da Tabela Nutricional e integração com porções.


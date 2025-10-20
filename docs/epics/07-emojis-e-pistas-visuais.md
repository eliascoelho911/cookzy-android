#+ Epic 07 — Emojis e Pistas Visuais

## Objetivo
Usar ícones/emoji para representar ações (⌛, 🔥 etc.) e facilitar leitura dos passos.

## Contexto
Melhoria de UX e scannability no preparo; alinhado ao design system.

## Escopo
- Em escopo: mapeamento de ações→ícones; tokens visuais; consistência em listas e passos.
- Fora de escopo (MVP): personalização avançada por usuário.

## Requisitos Funcionais
- Regras para renderizar ícones no fluxo de preparo.
- Acessibilidade: descrições alternativas e contraste.

## Critérios de Aceitação
- [ ] Ícones aparecem coerentes por tipo de ação.
- [ ] WCAG: não dependência apenas de cor/ícone.

## Dependências
- Design system (docs/front-end/01-design-system.md).

## Riscos
- Sobrecarga visual; mitigar com guidelines e testes de legibilidade.

## Métricas de Sucesso
- Melhora de satisfação subjetiva e tempo de compreensão.

## Stories sugeridas
1. Tabela de mapeamento ação→ícone/token.
2. Renderização nos passos + fallback de acessibilidade.


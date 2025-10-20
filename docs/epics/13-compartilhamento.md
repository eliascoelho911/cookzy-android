#+ Epic 13 — Compartilhamento

## Objetivo
Permitir compartilhar receitas via link/intent do sistema.

## Contexto
Amplia alcance e retorno ao app; integra com padrões do SO.

## Escopo
- Em escopo: compartilhamento básico de conteúdo/preview; intents nativas.
- Fora de escopo (MVP): backend público de páginas compartilhadas.

## Requisitos Funcionais
- Ação de compartilhar no Detalhe da Receita.
- Conteúdo mínimo: título, lista resumida de ingredientes, link interno.

## Critérios de Aceitação
- [ ] Sheet nativa abre com conteúdo correto.
- [ ] Retorno ao app abre a receita correta (deep link interno, se aplicável).

## Dependências
- Navegação/deep links internos.

## Riscos
- Expectativa de link público; mitigar com comunicação clara no MVP.

## Métricas de Sucesso
- Impulsiona aquisição orgânica e engajamento.

## Stories sugeridas
1. Ação de compartilhar e payload formatado.
2. Deep link interno para abrir Detalhe.


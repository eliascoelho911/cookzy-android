#+ Epic 09 — Vídeo com Pontos Ancorados

## Objetivo
Relacionar passos do preparo com timestamps do vídeo original e permitir navegação por âncoras.

## Contexto
Integra a experiência de vídeo ao passo a passo; útil para importação.

## Escopo
- Em escopo: mapeamento passo→timestamp; UI para pular para ponto do vídeo; destaque do passo atual.
- Fora de escopo (MVP): download/offline do vídeo; múltiplas fontes além de YouTube.

## Requisitos Funcionais
- Player com controle básico; saltos precisos para timestamps.
- Sincronização leve entre player e passos.

## Critérios de Aceitação
- [ ] Tocar âncora leva ao ponto correto no vídeo.
- [ ] Realce de passo atual ao buscar no player.

## Dependências
- Epic 04 (Importação) quando origem for vídeo; componentes de UI.

## Riscos
- Restrições de API/plataforma; mitigar com fallback de navegação manual.

## Métricas de Sucesso
- Aumento de conclusão de preparo com apoio visual.

## Stories sugeridas
1. Player de vídeo e API de âncoras.
2. UI de passos com navegação por timestamp.


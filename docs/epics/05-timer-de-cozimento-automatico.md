#+ Epic 05 — Timer de Cozimento Automático

## Objetivo
Detectar menções de tempo nos passos e permitir iniciar/pausar/redefinir com notificação ao término.

## Contexto
Pilar da assistência no preparo; integra com passos e mini-timer flutuante.

## Escopo
- Em escopo: parsing básico de tempos (ex.: “10 min”); múltiplas instâncias; notificação local.
- Fora de escopo (MVP): linguagem natural avançada; sincronização nuvem.

## Requisitos Funcionais
- Detecção de tempos por passo e ação para iniciar timer.
- Pausar/redefinir; notificação ao concluir; mini-timer acessível.

## Critérios de Aceitação (alto nível)
- [ ] Timers iniciam a partir do passo e disparam notificação.
- [ ] Múltiplos timers funcionam em paralelo.
- [ ] Estados preservados durante navegação no app.

## Dependências
- Notificações locais; contratos de state/effect; Player/Vídeo opcional (âncoras).

## Riscos
- Consumo de bateria/recursos; mitigar com boas práticas de lifecycle.

## Métricas de Sucesso
- Suporte direto à experiência fluida no preparo (meta global PRD).

## Stories sugeridas
1. Parser simples de duração em texto dos passos.
2. Gerenciador de múltiplos timers + mini-timer flutuante.
3. Notificações locais e restauração de estado.


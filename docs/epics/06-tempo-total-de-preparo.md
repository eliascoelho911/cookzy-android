#+ Epic 06 — Tempo Total de Preparo

## Objetivo
Registrar o tempo total entre início e fim do preparo e exibir ao usuário.

## Contexto
Complementa os timers por etapa; útil para planejamento e pós-uso.

## Escopo
- Em escopo: iniciar/encerrar sessão de preparo; exibir total; salvar com a receita (histórico básico).
- Fora de escopo (MVP): agregações históricas e analytics avançados.

## Requisitos Funcionais
- Botões/gestos para iniciar/encerrar preparo.
- Registro do total e exibição no Detalhe.

## Critérios de Aceitação
- [ ] Tempo total é registrado corretamente mesmo com navegação entre telas.
- [ ] Total exibido no Detalhe da receita/relato pós-uso.

## Dependências
- Navegação entre Detalhe/Preparo; persistência local.

## Riscos
- Perda de sessão em background; mitigar com lifecycle/foreground service quando necessário.

## Métricas de Sucesso
- Melhora percepção de utilidade durante o preparo; apoia retenção.

## Stories sugeridas
1. API de sessão de preparo (start/stop) + armazenamento.
2. UI para iniciar/encerrar e exibir total.


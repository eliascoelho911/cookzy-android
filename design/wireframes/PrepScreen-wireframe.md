# Preparo — Tela Focada — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Fluxo Cozinhar), docs/ui-architecture.md (Navegação/Componentes), integração com Prep Bar.

Objetivo
- Prover uma visualização focada e livre de distrações para executar o preparo passo a passo, com integração direta aos timers de etapa, leitura confortável e controles essenciais acessíveis.

Anatomia da tela (regiões)
- App Bar (fixa): voltar (←), título compacto (uma linha, ellipsis).
- Painel de Timers (colapsável, acima do Rodapé):
  - Estado colapsado (quando N > 1): pill destacado “⏱ Timers · N” com mini‑previews de progresso.
  - Estado expandido: lista vertical com todos os timers (rótulo do passo/receita, barra de progresso, mm:ss, ⏯, [Ir ao passo]).
  - Destaque do passo atual: a linha do timer do passo visível recebe ênfase (fundo tonal, barra primária, chip “Passo atual”).
  - N = 1: renderizar apenas a linha do timer (sem cabeçalho “Timers (1)”) e sem colapso.
- Avisos (fixo abaixo da App Bar): região dedicada a mensagens de estado curto.
  - Tipos: Mismatch (timer ≠ passo atual), Timer concluído, Erro de passo/ação, Nudge para iniciar timer.
  - Comportamento: 1 aviso visível por vez; fila (queue) com exibição sequencial.
  - Duração: Mismatch (persistente até resolver/dispensar), Concluído (~10s ou interação), Erro (persistente até fechar), Nudge (~8s ou iniciar/dispensar).
  - Ações inline contextuais: [Ir ao passo], [Abrir], [Dispensar].
- Conteúdo do Passo (rolável): texto do passo com destaques; gestos de scroll que avançam/retrocedem passo por limiar.
- Acessórios do Passo (inline, opcional): CTA de vídeo externo com timestamp.
- Rodapé (fixo): botões « Retroceder e » Avançar.

Elementos‑chave
- App Bar minimalista: voltar (←) e título compacto da receita (uma linha, ellipsis).
- Indicador de progresso: “Passo N/M”.
- Conteúdo do passo em tipografia ampliada; espaçamento generoso; destaques inline (ingredientes, temperaturas, tempos).
- Painel de Timers concentra todos os timers (inclusive o do passo atual) com controles por item;
- Rodapé com navegação de passo: « Retroceder e » Avançar (Avançar é a primária).
- CTA para vídeo externo com timestamp quando aplicável.

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

▉ Avisos (se houver)

  3) Asse no forno por [⏱ 30 min].
     Dica: vire na metade do tempo.

  [Abrir vídeo externo (00:23)]

    ┌────────────── Timers (2) ──────────────┐
    │  ▓▓▓░ 05:12   • Passo 3/6 (Molho)  ⏯ [Ir ao passo] │
    │  ▓░░░ 12:47   • Passo 5/6 (Massa)  ⏯ [Ir ao passo] │  ← colapsado pode mostrar previews ou apenas a pill
    └─────────────────────────────────────────┘

──────────────────────────────────────────
│  [ « Retroceder ]         [  » Avançar ] │
──────────────────────────────────────────
```

Variações por estado

1) Sem timer no passo (nudge para iniciar)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 2/6 │
└─────────────────────────────────────────┘

  2) Misture a massa por [⏱ 5 min].
     Use colher de pau.

  [Abrir vídeo externo (—)]

  💡 Sugestão: inicie um timer para acompanhar o passo.
  [⏱ Iniciar 5:00]

    ┌────────────── Timers (3) ──────────────┐
    │  ▓░░░ 14:10   • Passo 4/6 (Descansar) ⏯ [Ir ao passo] │  ← passo atual sem timer; lista mostra outros timers
    └─────────────────────────────────────────┘

──────────────────────────────────────────
│ [ « Retroceder ]   [  » Avançar ] │  ← incentivo explícito
──────────────────────────────────────────
```

2) Timer pausado no passo atual

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

  3) Asse no forno por [⏱ 30 min].

    ┌────────────── Timers (2) ──────────────┐
    │  ░░░░ 29:57   • Passo 3/6 (Assar)   ▶️ [Ir ao passo] │
    └─────────────────────────────────────────┘

──────────────────────────────────────────
│  [ « Retroceder ]         [  » Avançar ] │  ← Play/Pause no Painel de Timers
──────────────────────────────────────────
```

3) Timer concluído (destaque + incentivo a avançar)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

  ✓ Timer concluído — Molho pronto!
  3) Asse no forno por [⏱ 30 min].
 
    │  ✓ 00:00   • Passo 3/6 (Assar)     [Ir ao passo] │

──────────────────────────────────────────
│  [ « Retroceder ]       [  » Avançar ] │  ← realce por ~2s
──────────────────────────────────────────
```

4) Mismatch (timer ≠ passo atual)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 1/6 │
└─────────────────────────────────────────┘

  Aviso: Timer ativo no Passo 3/6 — [ Ir ao passo ]

  1) Preaqueça o forno a [🔥 180°C].

    ┌────────────── Timers (2) ──────────────┐
    │  ▓▓▓░ 05:12   • Passo 3/6 (Molho)  ⏯ [Ir ao passo] │
    └─────────────────────────────────────────┘

──────────────────────────────────────────
│   [ « Retroceder ]      [  » Avançar ] │  ← ⏯ controla o timer do Passo 3/6
──────────────────────────────────────────
```

5) Múltiplos timers (indicador destacado)

```
┌──────────────── App Bar ────────────────┐
│ ←  Bolonhesa                 Passo 2/8 │
└─────────────────────────────────────────┘

  2) Reduza o molho por [⏱ 15 min].

    ┌────────────── Timers (3) ──────────────┐
    │  ▓▓░░ 01:22   • Passo 2/8 (Reduzir) ⏯ [Ir ao passo] │
    └─────────────────────────────────────────┘

──────────────────────────────────────────
│  [ « Retroceder ]       [  » Avançar ] │  ← mostra o mais urgente
──────────────────────────────────────────
```

6) Com vídeo externo disponível

```
┌──────────────── App Bar ────────────────┐
│ ←  Ramen caseiro              Passo 4/7 │
└─────────────────────────────────────────┘

  4) Bata suavemente até emulsificar.

  [▶ Abrir vídeo externo (02:43)]

    │  ▓▓░░ 03:10   • Passo 4/7 (Emulsificar)  ⏯ [Ir ao passo] │

──────────────────────────────────────────
│  [ « Retroceder ]         [ » Avançar ] │
──────────────────────────────────────────
```

Gestos e navegação
- Scroll: avança/volta de passo ao atingir limiar de 60% da altura visível (com feedback de “snap” sutil). O título “Passo N/M” atualiza em sincronia.
- Botões de etapa:
  - « Retroceder: volta explicitamente para o passo anterior (desabilitado no Passo 1/1).
  - » Avançar: avança explicitamente para o próximo passo. Ação redundante ao gesto de scroll.
  - Atalhos: setas Esquerda/Direita mapeiam para «/»; manter foco visível.
- Play/Pause: no Painel de Timers, alterna o timer do passo atual (linha destacada) quando existir; se o passo não tiver timer detectado, use o chip [⏱ …] no texto para criar o timer.
- Back/Fechar: retorna à tela anterior mantendo estado do preparo (passo atual e timers).
- Painel de Timers (acima do player):
  - Toque no pill “⏱ Timers · N” ou arrastar para cima → expandir a lista completa de timers.
  - Na lista expandida: tocar em [Ir ao passo] navega para o passo do timer; ⏯ controla cada timer individual.
  - Toque fora/arrastar para baixo → colapsar o painel.

Região de Avisos — regras de prioridade
- Ordem de prioridade: Erro > Mismatch > Timer concluído > Nudge.
- Composição visual: container `surfaceVariant` com ícone por tipo (erro, timer, info), uma linha de texto com possível ação inline [Ir ao passo]/[Abrir]/[Dispensar]. Responsivo até 2 linhas sob `fontScale` alto.
- Interação: tocar fora não fecha; cada aviso tem alvo de toque ≥ 48dp para suas ações. Mismatch e Erro são persistentes; demais têm auto-ocultação.
- Nudge de visibilidade: quando N > 1 e o usuário abre a tela, exibir por ~4s o aviso “⏱ N timers ativos — veja o painel ‘Timers’ acima do player” (uma vez por sessão de preparo).
- Coexistência Avisos × Painel:
  - Painel expandido → ocultar Avisos (ou reduzir a chip sem CTA).
  - Aviso de Erro (`assertive`) → colapsar Painel automaticamente.
  - CTA “Ir ao passo” só aparece em Avisos quando o Painel estiver colapsado (evita duplicação com a lista).

Sincronização com timers
- Se um timer ativo pertence a outro passo (mismatch), exibir um aviso não intrusivo: “Timer ativo no Passo N/M — [Ir ao passo]”. A ação navega/focaliza o passo do timer quando o Painel está colapsado; se expandido, a ação equivalente está na lista.
- Concluído: quando um timer do passo atual termina, destacar a linha correspondente no Painel (fundo tonal + ícone ✓) e evidenciar o botão » Avançar por ~2s.
- Múltiplos timers: além do mais urgente, o Painel de Timers mostra o conjunto completo (expandido) ou previews (colapsado). Gestão completa ocorre via lista expandida.
  - Ordenação: menor tempo restante primeiro; empate → último iniciado.
  - Estabilidade de foco: não reordenar enquanto o usuário estiver interagindo (lock até inatividade de 2s); pin do “Passo atual” no topo durante a interação.
  - Auto‑expandir quando um 2º timer iniciar; auto‑colapsar após 5s de inatividade.

Divisão visual e comportamento de layout
- App Bar e Rodapé possuem elevação; a região de Avisos não rola com o conteúdo (fica ancorada abaixo da App Bar).
- O conteúdo do passo ocupa a área entre Avisos e Rodapé; o CTA de vídeo fica dentro dessa área, acima do Rodapé.
- Em orientação landscape, manter as mesmas regiões; garantir que o Rodapé permaneça tocável e que a região de Avisos não sobreponha conteúdo crítico.

Acessibilidade
- Ordem de foco previsível: título → conteúdo do passo → vídeo (se houver) → controles do rodapé.
- Ícones com `contentDescription` claros; `stateDescription` para ⏯ (“Rodando”/“Pausado”).
- Alvos ≥ 48dp (especialmente os botões « Retroceder e » Avançar). Suporte a `fontScale` até 200% sem truncar conteúdo crítico.
- Leitura por leitor de tela: anunciar “Passo N de M”. Destaques clicáveis (ingrediente/temperatura/tempo) com rótulos completos.
- Região de Avisos: usar live region (`polite` para Concluído/Nudge, `assertive` para Erro). Ações acessíveis por teclado e leitor de tela.
- Painel de Timers: no colapsado anunciar “N timers ativos. Toque para expandir”. No expandido, role=list com cada item anunciando rótulo, progresso e tempo restante; ações ⏯/[Ir ao passo] com labels claros. A linha do passo atual deve ter `selected=true` e `stateDescription="Passo atual"`.

Estados
- Carregando: esqueleto simples do conteúdo + placeholder dos controles.
- Vazio/erro: mensagem e ação de retorno.
- Sucesso: conforme wireframe.

Responsividade
- Mobile (MVP): 1 coluna, leitura confortável; rodapé fixo.
- ≥ 840 dp: centralizar conteúdo com largura de leitura (~640 dp) e manter controles alinhados ao rodapé.

Notas para Dev
- Rota dedicada: `recipe/{id}/prep` (destination exclusivo para preparo focado).
- Assinatura sugerida:
  ```kotlin
  @Composable
  fun RecipePrepScreen(
    state: PrepUiState,
    onToggleTimer: () -> Unit,
    onPrevStep: () -> Unit,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    onOpenExternalVideo: (timestamp: Long) -> Unit,
    onGoToTimerStep: () -> Unit,
  )
  ```
- `PrepUiState` deve conter: `currentStepIndex`, `stepsCount`, `hasTimerForStep`, `remaining`, `running`, `activeTimersCount`, `mismatch`.
- Preservar `currentStepIndex` e `scroll` ao sair/voltar. O gesto de scroll deve sinalizar claramente avanço/retrocesso de passo (por limiar de altura ou gesto dedicado).
- Prévias: passo sem timer; com timer rodando; pausado; concluído; mismatch; múltiplos timers.
- Painel de Timers (MVP): `timers: List<TimerUi>`, `onToggle(timerId)`, `onGoTo(timerId)`, `expanded: Boolean`, `onExpandToggle()`; animação simples de altura/alpha.
- Tokens do Painel: altura da linha 56–64dp; barra de progresso 4dp; fonte monoespaçada para mm:ss; cores running=primary, paused=onSurfaceVariant, finished=success; destacar “Passo atual” com container tonal.

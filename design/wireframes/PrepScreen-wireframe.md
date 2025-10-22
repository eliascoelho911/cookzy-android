# Preparo — Tela Focada — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Fluxo Cozinhar), docs/ui-architecture.md (Navegação/Componentes), integração com Prep Bar.

Objetivo
- Prover uma visualização focada e livre de distrações para executar o preparo passo a passo, com integração direta aos timers de etapa, leitura confortável e controles essenciais acessíveis.

Elementos‑chave
- App Bar minimalista: voltar (←) e título compacto da receita (uma linha, ellipsis).
- Indicador de progresso: “Passo N/M”.
- Conteúdo do passo em tipografia ampliada; espaçamento generoso; destaques inline (ingredientes, temperaturas, tempos).
- Controles fixos no rodapé: ⏯ Play/Pause (quando o passo tiver timer ativo), » Avançar passo (sempre visível). Tempo restante (mm:ss) quando houver timer.
- CTA para vídeo externo com timestamp quando aplicável.

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

  3) Asse no forno por [⏱ 30 min].
     Dica: vire na metade do tempo.

  [Abrir vídeo externo (00:23)]

──────────────────────────────────────────
│   ⏯  29:57                 [  » Avançar ] │
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

──────────────────────────────────────────
│        [ ⏱ Iniciar 5:00 ]   [  » Avançar ] │  ← incentivo explícito
──────────────────────────────────────────
```

2) Timer pausado no passo atual

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

  3) Asse no forno por [⏱ 30 min].

──────────────────────────────────────────
│   ▶️  29:57                 [  » Avançar ] │  ← stateDescription: "Pausado"
──────────────────────────────────────────
```

3) Timer concluído (destaque + incentivo a avançar)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 3/6 │
└─────────────────────────────────────────┘

  ✓ Timer concluído — Molho pronto!
  3) Asse no forno por [⏱ 30 min].

──────────────────────────────────────────
│   ⏯  00:00              [  » Avançar — Próximo ] │  ← realce por ~2s
──────────────────────────────────────────
```

4) Mismatch (timer ≠ passo atual)

```
┌──────────────── App Bar ────────────────┐
│ ←  Panquecas fofas           Passo 1/6 │   2×
└─────────────────────────────────────────┘

  Aviso: Timer ativo no Passo 3/6 — [ Ir ]

  1) Preaqueça o forno a [🔥 180°C].

──────────────────────────────────────────
│   ⏯  05:12                 [  » Avançar ] │  ← ⏯ controla o timer do Passo 3/6
──────────────────────────────────────────
```

5) Múltiplos timers (badge)

```
┌──────────────── App Bar ────────────────┐
│ ←  Bolonhesa                 Passo 2/8 │   3×
└─────────────────────────────────────────┘

  2) Reduza o molho por [⏱ 15 min].

──────────────────────────────────────────
│   ⏯  01:22                 [  » Avançar ] │  ← mostra o mais urgente
──────────────────────────────────────────
```

6) Com vídeo externo disponível

```
┌──────────────── App Bar ────────────────┐
│ ←  Ramen caseiro              Passo 4/7 │
└─────────────────────────────────────────┘

  4) Bata suavemente até emulsificar.

  [▶ Abrir vídeo externo (02:43)]

──────────────────────────────────────────
│   ⏯  03:10                 [  » Avançar ] │
──────────────────────────────────────────
```

Gestos e navegação
- Scroll: avança/volta de passo ao atingir limiares de leitura (com feedback sutil). O título “Passo N/M” atualiza em sincronia.
- Botão » Avançar: avança explicitamente para o próximo passo (mesmo se o usuário estiver lendo lentamente). Ação redundante ao gesto de scroll.
- Play/Pause: alterna o timer do passo atual, quando existir; se o passo não tiver timer detectado, o botão ⏯ não aparece (em vez disso, o chip [⏱ …] no texto sugere criar o timer).
- Back/Fechar: retorna à tela anterior mantendo estado do preparo (passo atual e timers).

Sincronização com timers
- Se um timer ativo pertence a outro passo (mismatch), exibir um aviso não intrusivo: “Timer ativo no Passo N/M — [Ir]”. Tocar em [Ir] rola e focaliza o passo do timer.
- Concluído: quando um timer do passo atual termina, destacar o rodapé por ~2s e evidenciar o botão » Avançar.
- Múltiplos timers: mostrar o mais urgente no rodapé; badge “N×” no App Bar (sem seletor no MVP). Gestão completa ocorre ao navegar para os respectivos passos.

Acessibilidade
- Ordem de foco previsível: título → conteúdo do passo → vídeo (se houver) → controles do rodapé.
- Ícones com `contentDescription` claros; `stateDescription` para ⏯ (“Rodando”/“Pausado”).
- Alvos ≥ 48dp (especialmente o botão » Avançar). Suporte a `fontScale` até 200% sem truncar conteúdo crítico.
- Leitura por leitor de tela: anunciar “Passo N de M”. Destaques clicáveis (ingrediente/temperatura/tempo) com rótulos completos.

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
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    onOpenExternalVideo: (timestamp: Long) -> Unit,
    onGoToTimerStep: () -> Unit,
  )
  ```
- `PrepUiState` deve conter: `currentStepIndex`, `stepsCount`, `hasTimerForStep`, `remaining`, `running`, `activeTimersCount`, `mismatch`.
- Preservar `currentStepIndex` e `scroll` ao sair/voltar. O gesto de scroll deve sinalizar claramente avanço/retrocesso de passo (por limiar de altura ou gesto dedicado).
- Prévias: passo sem timer; com timer rodando; pausado; concluído; mismatch; múltiplos timers.

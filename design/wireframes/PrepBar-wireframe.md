# Prep Bar — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Barra de Preparo/mini‑timer) • docs/ui-architecture.md (Inventário de Componentes: PrepBar).

Objetivo
- Manter um mini‑timer persistente no rodapé, visível em todo o app enquanto houver timer de etapa ativo, oferecendo retorno rápido à Tela de Preparo (focada) e controles essenciais (play/pausa/avançar passo). Não haverá cronômetro global de sessão de preparo.

Elementos‑chave
- Contêiner fixo no rodapé (full‑width), elevado acima do conteúdo; respeita barras do sistema (gesture/nav bar).
- Toque na área principal → abre a Tela de Preparo (focada) e posiciona no passo do timer.
- Título compacto (receita e/ou passo) e tempo restante em destaque (mm:ss) quando existir timer de etapa. Usar fonte monoespaçada para mm:ss.
- Controles: Play/Pause (toggle) e Avançar passo (»). O botão “encerrar” não existe; encerrar/editar timers acontece na aba Preparo.
- Múltiplos timers: exibir o próximo a concluir; badge “2×/3×” indica quantidade. Toque abre a Tela de Preparo para gestão completa pelo Painel de Timers.
- Não aparece dentro da Tela de Preparo focada (RecipePrepScreen) para evitar duplicação de controles com o Rodapé e Painel de Timers.

Wireframe (Mobile)

```
… conteúdo da tela …

────────────────────────────────────────────────────────────────
│  🍳 Panquecas — Passo 3/6           08:21      ⏯     »    2× │
────────────────────────────────────────────────────────────────
   ▲ toque (área principal) abre Tela de Preparo  ▲     ▲    ▲
                                                 play   avança badge
```

Variações de estado
- Rodando
  - Fundo em `surfaceContainer` com leve realce; tempo decrementa por segundo.
  - Ícone ⏯ indica “pausar”.

```
────────────────────────────────────────────────────────────────
│  🍝 Bolonhesa — Timer do molho       12:47      ⏯     »       │
────────────────────────────────────────────────────────────────
```

- Pausado
  - Fundo levemente desaturado; tempo congelado; `stateDescription = "Pausado"`.
  - Ícone ⏯ indica “retomar”.

```
────────────────────────────────────────────────────────────────
│  🍝 Bolonhesa — Timer do molho       12:47     ▶️     »       │
────────────────────────────────────────────────────────────────
```

- Concluído
  - Sinal sonoro/vibração + destaque por 2s; mantém barra até ação do usuário.
  - Ações: abrir Preparo (toque) ou avançar passo (»), quando aplicável. Reiniciar/encerrar acontecem na aba Preparo.
  - Ação rápida opcional: [+1:00] (adiciona 1 min ao timer concluído e volta a “Rodando”), alinhado ao comportamento da Tela de Preparo quando o Painel/Aviso está colapsado.

```
────────────────────────────────────────────────────────────────
│  ✓ Timer concluído — Molho pronto!        [+1:00]  Abrir   »  │
────────────────────────────────────────────────────────────────
```

- Múltiplos timers
  - Exibe o mais urgente; badge “N×” indica a contagem total.
  - Prioridade: menor tempo restante; em empate, último iniciado.

– Mismatch (timer ≠ passo atual)
- Situação: um timer está rodando para o Passo N, mas a interface de Preparo está posicionada em outro passo (usuário avançou/retrocedeu pelo texto/scroll).
- Comportamento da barra: a área principal mostra o passo do timer; surge CTA “Ir ao passo do timer”. O botão “Avançar (»)” continua avançando o passo corrente (não o do timer).

```
────────────────────────────────────────────────────────────────
│  ⏱ Passo 3/6 rodando (Molho)         05:12   ⏯   »    2×     │
│  Você está no Passo 1/6.   [ Ir ao passo do timer ]          │
────────────────────────────────────────────────────────────────
```

- Toque na área principal ou no CTA “Ir ao passo do timer” → abre a Tela de Preparo (focada) e foca o Passo 3/6 (timer) mantendo-o como timer ativo.

Regras de exibição (persistência)
- Aparece quando: houver ao menos um timer de etapa rodando ou pausado.
- Some quando: não restarem timers ativos/pausados.
- Não renderizar na `RecipePrepScreen` (Tela de Preparo focada); nessa tela, a gestão ocorre via Painel de Timers e Rodapé próprios.
- Navegação: toque (fora dos ícones) sempre abre a Tela de Preparo (focada) da receita ativa.

Interações
- Toque na área principal → abre a Tela de Preparo (focada).
- ⏯ Play/Pause → alterna estado do timer focado; anuncia “Timer pausado/retomado”.
- » Avançar → avança para o próximo passo da receita (mesmo fora da Tela de Preparo). Voltar de passo acontece por gesto/scroll dentro da Tela de Preparo.
- Badge “N×” → indica contagem; a gestão de múltiplos é feita na Tela de Preparo, via Painel de Timers (não há carrossel na barra).
 - [+1:00] (quando visível) → adiciona tempo ao timer mostrado e muda para “Rodando”.

Acessibilidade
- Role: `button` para a área principal; ícones com `contentDescription` claros (“Pausar timer”, “Retomar timer”, “Avançar passo”).
- `stateDescription`: “Rodando”/“Pausado”/“Concluído”; anunciar tempo restante (ex.: “oito minutos e vinte e um segundos restantes”).
- Concluído: expor ação com label acessível “Adicionar 1 minuto ao timer” quando a ação rápida estiver presente. Preferir live region `polite` para anúncio de conclusão fora da Tela de Preparo.
- Alvos ≥ 48dp; ordem de foco previsível: área principal → Play/Pause → Avançar.
- Respeita `fontScale` até 200% sem truncar o tempo; overflow elíptico no título.

Responsividade
- Mobile (MVP): barra full‑width; altura ~64dp; paddings 16dp; ícones 24dp.
- ≥ 840 dp: manter a mesma barra centralizada horizontalmente; sem duplicar controles.

 Sincronização com timers (alinhado à Tela de Preparo)
 - Seleção do item exibido (quando N>1): menor tempo restante primeiro; empate → último iniciado.
 - Estabilidade: evitar reordenações visuais enquanto o usuário interage com a barra; atualizar apenas mm:ss (monoespaçado) e ícone de estado.
 - Mismatch: quando presente, a barra reflete o passo do timer; o botão » atua sobre o `currentStepIndex` persistido da sessão de preparo (não muda o alvo do timer).

 Notas para Dev
  - Composable sugerido (alinhado a docs/ui-architecture.md):
  ```kotlin
  @Composable
  fun PrepBar(
    title: String,
    remaining: Duration?,
    running: Boolean,
    activeCount: Int,
    onToggle: () -> Unit,
    onNextStep: () -> Unit,
    onOpenPrep: () -> Unit,
    mismatch: Boolean,
    onGoToTimerStep: () -> Unit,
    onAddTime: (amount: Duration) -> Unit = {},
  ) { /* … */ }
  ```
  - Estados obrigatórios de preview: Rodando; Pausado; Concluído (com [+1:00]); Múltiplos timers (badge > 1); Mismatch (timer ≠ passo atual).
  - Semântica: `Modifier.semantics { stateDescription = … }` e `contentDescription` nos ícones; `testTag` para automação. A ação rápida deve ter `contentDescription` “Adicionar 1 minuto ao timer”.
  - Navegação: `onOpenPrep()` direciona a `recipe/{id}/prep` (Tela de Preparo focada); `onGoToTimerStep()` foca o passo do timer dentro dessa tela.
  - Cores/tokens: usar Material 3 + tokens do tema do app; contrastes revisados para claro/escuro; mm:ss com fonte monoespaçada.

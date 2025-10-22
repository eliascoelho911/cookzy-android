# Prep Bar — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Barra de Preparo/mini‑timer) • docs/ui-architecture.md (Inventário de Componentes: PrepBar).

Objetivo
- Manter um mini‑timer persistente no rodapé, visível em todo o app enquanto houver preparo ativo, oferecendo retorno rápido à aba Preparo e controles essenciais (play/pausa/encerrar).

Elementos‑chave
- Contêiner fixo no rodapé (full‑width), elevado acima do conteúdo; respeita barras do sistema (gesture/nav bar).
- Toque na área principal → abre Detalhe da Receita na aba Preparo (retoma o passo atual).
- Título compacto (receita e/ou passo) e tempo restante em destaque (mm:ss).
- Ação Play/Pause (toggle) e Encerrar (✕). Undo via snackbar quando encerrar.
- Múltiplos timers: exibir o próximo a concluir; badge “2×/3×” indica quantidade. Toque abre a aba Preparo para gestão completa.

Wireframe (Mobile)

```
… conteúdo da tela …

────────────────────────────────────────────────────────────────
│  🍳 Panquecas — Passo 3/6           08:21      ⏯     ✕    2× │
────────────────────────────────────────────────────────────────
   ▲ toque (área principal) abre Aba Preparo      ▲     ▲    ▲
                                                 play   enc. badge
```

Variações de estado
- Rodando
  - Fundo em `surfaceContainer` com leve realce; tempo decrementa por segundo.
  - Ícone ⏯ indica “pausar”.

```
────────────────────────────────────────────────────────────────
│  🍝 Bolonhesa — Timer do molho       12:47      ⏯     ✕       │
────────────────────────────────────────────────────────────────
```

- Pausado
  - Fundo levemente desaturado; tempo congelado; `stateDescription = "Pausado"`.
  - Ícone ⏯ indica “retomar”.

```
────────────────────────────────────────────────────────────────
│  🍝 Bolonhesa — Timer do molho       12:47     ▶️     ✕       │
────────────────────────────────────────────────────────────────
```

- Concluído
  - Sinal sonoro/vibração + destaque por 2s; mantém barra até ação do usuário.
  - Ações: abrir Preparo (toque) ou ✕ para dispensar; opção de “Reiniciar” dentro da aba Preparo.

```
────────────────────────────────────────────────────────────────
│  ✓ Timer concluído — Molho pronto!              Abrir    ✕    │
────────────────────────────────────────────────────────────────
```

- Múltiplos timers
  - Exibe o mais urgente; badge “N×” indica a contagem total.
  - Prioridade: menor tempo restante; em empate, último iniciado.

Regras de exibição (persistência)
- Aparece quando: (a) houver ao menos um timer rodando ou pausado, ou (b) uma sessão de preparo estiver ativa (mesmo sem timer; mostra “Continuar preparo”).
- Some quando: todos os timers forem encerrados e a sessão de preparo for finalizada; ou usuário dispensar estado “Concluído” e não restarem timers/ sessão.
- Navegação: toque (fora dos ícones) sempre abre a aba Preparo da receita ativa.

Interações
- Toque na área principal → abre Detalhe → aba Preparo (mantém scroll/etapa).
- ⏯ Play/Pause → alterna estado do timer focado; anuncia “Timer pausado/retomado”.
- ✕ Encerrar → para e remove o timer focado; snackbar “Timer encerrado” com [Desfazer]. Se não restarem timers e não houver sessão ativa, a barra some.
- Badge “N×” → indica contagem; a gestão de múltiplos é feita na aba Preparo (não há carrossel de timers no MVP).

Acessibilidade
- Role: `button` para a área principal; ícones com `contentDescription` claros (“Pausar timer”, “Retomar timer”, “Encerrar timer”).
- `stateDescription`: “Rodando”/“Pausado”/“Concluído”; anunciar tempo restante (ex.: “oito minutos e vinte e um segundos restantes”).
- Alvos ≥ 48dp; ordem de foco previsível: área principal → Play/Pause → Encerrar.
- Respeita `fontScale` até 200% sem truncar o tempo; overflow elíptico no título.

Responsividade
- Mobile (MVP): barra full‑width; altura ~64dp; paddings 16dp; ícones 24dp.
- ≥ 840 dp: manter a mesma barra centralizada horizontalmente; sem duplicar controles.

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
    onClose: () -> Unit,
    onOpenPrep: () -> Unit,
  ) { /* … */ }
  ```
- Estados obrigatórios de preview: Rodando; Pausado; Concluído; Múltiplos timers (badge > 1).
- Semântica: `Modifier.semantics { stateDescription = … }` e `contentDescription` nos ícones; `testTag` para automação.
- Navegação: `onOpenPrep()` direciona a `recipe/{id}` com aba Preparo selecionada (preservar/push seguindo Navigation Compose).
- Cores/tokens: usar Material 3 + tokens do tema do app; contrastes revisados para claro/escuro.


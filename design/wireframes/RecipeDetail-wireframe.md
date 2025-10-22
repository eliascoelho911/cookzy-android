# Recipe Detail — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Detalhe da Receita, Preparo, Componentes) e docs/ui-architecture.md.

Objetivo
- Centro de verdade da receita; iniciar preparo, ajustar porções, acessar ingredientes/preparo/nutrição e compartilhar.

Elementos‑chave
- App Bar com voltar e ações contextuais (ex.: Compartilhar).
- Título da receita e CTA “Abrir vídeo externo (timestamp)” quando houver origem.
- Botão primário “Iniciar preparo”.
- Botão “Medidas” (abre Conversor de Medidas como bottom sheet).
- Stepper de Porções inline (1–99) posicionado abaixo do “Iniciar preparo” e ao lado do botão “Medidas”.
- Tabs: Ingredientes | Preparo | Nutrição.
- Barra de Preparo (mini‑timer) persistente no rodapé quando houver preparo ativo.

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│ ←  Detalhe                         [⤴] │  ← ação Compartilhar (abre sheet)
└─────────────────────────────────────────┘

  Título da Receita
  [▶ Abrir vídeo externo (ícone do Youtube, Instagram...)]       ← se houver origem

  [            Iniciar preparo            ]
  [ Medidas ]  [ − 2 + ]
     │               └─ Stepper de Porções (1–99)
     └─ abre Sheet: Conversor de Medidas

  ┌──────── Tabs ─────────────────────┐
  │ Ingredientes | Preparo | Nutrição │
  └───────────────────────────────────┘

  Conteúdo da aba selecionada
  - Ingredientes: • 200 g farinha  • 1 ovo …
  - Preparo: 1) Misture…  [▶ Iniciar/pausar timer quando houver]
             2) Asse…
             [Abrir vídeo externo (00:23)] no fim do passo (quando houver)
  - Nutrição: kcal, macros, porção

────────────────────────────────────────────────────────────────
│  Prep Bar  |  08:21  |  ⏯  |  ✕  │   ← Barra de Preparo persistente
────────────────────────────────────────────────────────────────
```

Estados
- Carregando: indicador central.
- Erro: mensagem + ação “Voltar”.
- Sucesso: conforme wireframe.

Interações
- “Iniciar preparo” → seleciona aba Preparo e inicia fluxo; Prep Bar aparece e persiste em todo o app.
- “Medidas” → abre Conversor (bottom sheet) com foco no primeiro campo.
- Stepper (1–99) → recalcula quantidades da aba Ingredientes; persiste por receita.
- Compartilhar → sheet com opções do sistema.
- CTA “Abrir vídeo externo” → Intent ACTION_VIEW (fallback navegador).

Acessibilidade
- Stepper com role=adjustable; anunciar mudanças; limites com feedback discreto.
- Tabs com foco/indicador visível; ordem de leitura previsível.
- Prep Bar com `stateDescription` (play/pause) e anúncio do tempo restante.

Responsividade
- MVP: 1 coluna. Futuro (≥ 840 dp): multipainel (Ingredientes | Preparo) fora deste escopo.

Notas para Dev
- Arquivo base: app/src/main/java/.../recipedetail/RecipeDetailScreen.kt
- Adicionar previews: carregando; erro; sucesso (cada aba selecionada).


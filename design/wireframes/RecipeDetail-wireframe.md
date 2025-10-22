# Recipe Detail — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Detalhe da Receita, Preparo, Componentes) e docs/ui-architecture.md.

Objetivo
- Centro de verdade da receita; iniciar preparo, ajustar porções, acessar ingredientes/preparo/nutrição e compartilhar.

Elementos‑chave
- App Bar com voltar e ações contextuais: Compartilhar e Editar.
- Hero no topo com imagem da receita; overlay com botão Play (abre vídeo externo quando houver origem).
- Título da receita com tempo de preparo ao lado.
- Grid de Resumo Nutritivo acima do botão “Iniciar preparo”: ícone + texto para Carbs, Proteínas, Kcal, Gordura.
- Botão primário “Iniciar preparo”.
- Botão “Medidas” (abre Conversor de Medidas como bottom sheet).
- Stepper de Porções inline (1–99) posicionado abaixo do “Iniciar preparo” e ao lado do botão “Medidas”.
- Tabs: Ingredientes | Preparo | Nutrição.
- Barra de Preparo (mini‑timer) persistente no rodapé quando houver preparo ativo.

Wireframe (Mobile)

```
┌──────────────── App Bar ────────────────┐
│ ←  Detalhe                    [⤴] [✎] │  ← Compartilhar | Editar
└─────────────────────────────────────────┘

┌────────────── Hero (Imagem da Receita) ──────────────┐
│ [ Cover 16:9 ]                              [▶]      │  ← Play overlay (vídeo externo)
└──────────────────────────────────────────────────────┘

  Título da Receita     • ⏱️ 25 min

  Resumo Nutritivo
  ┌───────┬─────────┬┐
  │ Carbs │ Proteínas│ ← ícone + texto por item
  └───────┴─────────┴┘
  ┌─────────┬─────────┐
  ││  Kcal   │ Gordura│ ← ícone + texto por item
  └─────────┴─────────┘
  
  [            Iniciar preparo            ]
  
  [ Medidas ]  [ − 2 + ]
     │               └─ Stepper de Porções (1–99)
     └─ abre Sheet: Conversor de Medidas

  ┌──────── Tabs ─────────────────────┐
  │ Ingredientes | Preparo | Nutrição │
  └───────────────────────────────────┘

  Conteúdo da aba selecionada
  - Ingredientes: 
    • **200 g** farinha de trigo  ← quantidade em negrito
    • **1** ovo …  ← quantidade em negrito
  - Preparo:
      1) Preaqueça o forno a  
         [🔥 180°C]  ← destaque temperatura (ícone + cor de aviso)
      2) Em uma tigela, misture a 
         __farinha de trigo__   ← ingrediente sublinhado (dotted). Tap → tooltip “400g de farinha de trigo”.
      3) Asse no forno por  [⏱ 30 min]  ← destaque timer (tap → sugere criar timer do passo)
         [Abrir vídeo externo (00:23)] no fim do texto (quando houver)
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
- CTA “Abrir vídeo externo” (no Hero e no passo) → Intent ACTION_VIEW (fallback navegador).
- Editar (ícone ✎ na App Bar) → navega para o Editor da receita atual.

 Interações de Preparo — Destaques
 - Ingrediente (tooltip): palavras mapeadas para ingredientes aparecem com sublinhado pontilhado; toque exibe tooltip ancorado com “quantidade + nome”; ações secundárias: “Converter medidas” (sheet) e “Copiar”. Dismiss por tap fora/voltar.
 - Temperatura: padrões como “180°C/ºC” recebem chip inline [🔥 valor]; cor usa `warning/onSecondaryContainer`; long-press oferece “Converter °C/°F”. Mudanças de temperatura entre passos podem animar com micro-pulso.
 - Timer: padrões “30 min/1 h/45m” recebem chip [⏱ valor]; tap sugere criar timer do passo com esse valor; ao confirmar, botão do passo vira play/pause e Prep Bar ativa com o tempo restante.

Acessibilidade
- Stepper com role=adjustable; anunciar mudanças; limites com feedback discreto.
- Tabs com foco/indicador visível; ordem de leitura previsível.
- Prep Bar com `stateDescription` (play/pause) e anúncio do tempo restante.
- Hero com `contentDescription` (imagem); botão Play com label da plataforma/tempo.
- Ícones do resumo nutritivo com labels claros (ex.: “Carboidratos: 45 g”).
- Destaques inline acessíveis: chips de temperatura/tempo com labels completos (ex.: “Temperatura: 180 graus Celsius”, “Tempo: 30 minutos”).
- Tooltip de ingrediente: `role=dialog`, foco inicial no conteúdo; swipe down/Voltar para fechar.

Responsividade
- MVP: 1 coluna. Futuro (≥ 840 dp): multipainel (Ingredientes | Preparo) fora deste escopo.

Notas para Dev
- Arquivo base: app/src/main/java/.../recipedetail/RecipeDetailScreen.kt
- Adicionar previews: carregando; erro; sucesso (cada aba selecionada).

### Aba Ingredientes — Detalhes

- Formatação: cada linha exibe o texto do ingrediente com a quantidade destacada em negrito (ex.: “**200 g** farinha de trigo”).
- Atualização dinâmica: ao ajustar o Stepper de porções, as quantidades são recalculadas e o destaque em negrito acompanha o novo valor.
- Conversão rápida (opcional): long‑press sobre a quantidade pode abrir o Conversor de Medidas (sheet) ancorado ao item.
- Acessibilidade: leitor de tela anuncia quantidade, unidade e ingrediente de forma completa; ordem de foco segue a lista; tamanho de toque ≥ 48dp quando houver ações por item.

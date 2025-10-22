# FAB Sheet — Adicionar receita — Wireframe (MVP)

Fonte: docs/front-end-spec.md (FAB + Sheet) • docs/ui-architecture.md (Componentes & Padrões de Sheet).

Objetivo
- Oferecer ponto de entrada rápido para criar uma nova receita via Importar (colar link) ou Adicionar manualmente (Editor), mantendo acessibilidade e clareza.

Elementos‑chave
- Modal Bottom Sheet (Material 3) acionada pelo FAB “Adicionar receita”.
- Cabeçalho com título “Adicionar receita”, handle de arraste e ação de fechar.
- Lista de opções com ícone, título e descrição curta:
  - Importar receita — “Cole um link (YouTube, Instagram, blogs)”.
  - Adicionar manualmente — “Abrir editor de receita”.
- Scrim de fundo; dismiss por arrastar, tap fora ou Back.

Wireframe (Mobile)

```
  ┌──────────────── App Bar ────────────────┐
  │  Receitas                        [🔍]   │
  └─────────────────────────────────────────┘

  … conteúdo da Lista/Grade …                           ⊕  ← FAB

  ────────────────────────── Scrim ───────────────────────────────
┌──────────────────────── Bottom Sheet ───────────────────────────┐
│             ───                                                 │  ← drag handle
│  Adicionar receita                                  [ Fechar ]  │
├─────────────────────────────────────────────────────────────────┤
│  [ ⤓ ]  Importar receita                                       │
│         Cole um link (YouTube, Instagram, blog)                 │
│                                                                 │
│  [ ✎ ]  Adicionar manualmente                                   │
│         Abrir editor de receita                                 │
├─────────────────────────────────────────────────────────────────┤
│      (arraste para baixar • toque fora • Back para fechar)      │
└─────────────────────────────────────────────────────────────────┘
```

Estados
- Padrão: duas opções ativas. Foco inicial no primeiro item (Importar).
- Ação indisponível: se alguma opção precisar ser desabilitada por política/feature flag, manter descrição e indicar estado “desativado”.
- Erro: não se aplica no sheet; erros acontecem nas telas subsequentes (ex.: falha ao importar).

Interações
- FAB → abre a sheet com animação padrão (translateY + fade do scrim). `skipPartiallyExpanded = true` no MVP.
- Importar receita → navega para “Escolher origem/colar link” do fluxo de importação.
- Adicionar manualmente → navega para o Editor de Receita (tela em branco com mínimos).
- Fechamento: arrastar para baixo, tap fora do sheet, Back, ou botão [Fechar].
- Tamanho de toque ≥ 48dp; linha inteira clicável; ícones 24dp.

Acessibilidade
- Sheet anuncia abertura (“Adicionar receita, sheet”); ordem de foco: título → Importar → Manual → Fechar.
- Itens com role=button e `contentDescription` completos (ex.: “Importar receita. Cole um link de YouTube, Instagram ou blog”).
- Suporte a teclado: Enter/Space ativa item; Esc/Back fecha; foco visível.
- Contraste adequado em light/dark; respeitar fontScale até 200% sem truncar descrições.

Responsividade
- Mobile (MVP): sheet em altura ajustada ao conteúdo; largura total.
- ≥ 840 dp: sheet centralizada/ancorada ao bottom, largura máx. ~640 dp conforme ui-architecture.

Notas para Dev
- Base: usar `CookzyModalBottomSheet` para padronizar header (título + IconButton fechar) e footer opcional.
- Exemplo:
  ```kotlin
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun AddRecipeFabSheet(open: Boolean, onImport: () -> Unit, onManual: () -> Unit, onClose: () -> Unit) {
    if (!open) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    CookzyModalBottomSheet(
      title = "Adicionar receita",
      sheetState = sheetState,
      onDismissRequest = onClose,
    ) {
      // Conteúdo de lista de opções
      // (cada linha clicável com ícone + título + descrição)
    }
  }
  ```
- Estado: `rememberModalBottomSheetState(skipPartiallyExpanded = true)`.
- Semântica: fornecer `confirmValueChange` se futuramente houver operações pendentes.
- Ícones: `link` (Importar), `edit` (Manual). Logos de plataformas não são obrigatórios no MVP.
- Prévias necessárias: fechado; aberto; fontScale 2.0; tema claro/escuro.

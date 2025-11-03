# Inventário de Componentes (Compose)

Tabela de referência dos principais `@Composable`s a implementar/reutilizar. Servirá para handoff e para organizar prévias e testes.

| Componente | Propósito | Props (chave) | Estados | A11y | Prévias Requeridas |
|---|---|---|---|---|---|
| AppTopBar | Barra superior com título/ações (suporta busca inline opcional) | `title: String`, `onBack: (() -> Unit)?`, `actions: @Composable RowScope.() -> Unit`, `search: SearchUi?`, `style: AppTopBarStyle = Default` | Default, Scrolled, Search, Transparent | botão voltar com label; ordem de foco; campo de busca com hint/clear; Transparent requer contraste com sombra/blur opcional | Default; Search; Transparent sobre Hero |
| TitleBlock | Títulos de tela/seção com slots composables para título/subtítulo + metadado textual e ação opcional (sem ícones dedicados) | `title: @Composable () -> Unit`, `subtitle: (@Composable () -> Unit)?`, `meta: AnnotatedString?`, `style: TitleBlockStyle` (`Large`/`Small`), `trailing: @Composable (() -> Unit)?` | Large, Small | heading semantics; CompositionLocalProvider define estilos padrão; ação com label | Large (descrição principal); Small (seção com CTA); fontScale 2x |
| RecentRecipesCarousel | Carrossel de receitas recentes | `items: List<Recipe>`, `onClick(Recipe)`, `peekDp: Dp` | vazio | anunciar “deslize p/ ver mais”; foco previsível | vazio; 1 item; vários |
| BookFilterCarousel | Carrossel de livros (filtro) | `books: List<Book>`, `selected: Book?`, `onSelect(Book?)` | selected/unselected | role=tab/toggle; item “Todos” com label | sem seleção; com seleção |
| ListGridToggle | Alternância lista ↔ grade | `isGrid: Boolean`, `onToggle(Boolean)` | list/grid | role=toggle; descriptions | list; grid |
| RecipeCard | Card de receita em listas | `title`, `time: Duration?`, `livrosDeReceitas: List<String>`, `onClick` | loading, error, vazio | contentDescription no card; foco visível | normal; loading; error |
| RecipeList | Lista vertical de receitas com `RecipeCard.List` | `state: RecipeListState`, `listState: LazyListState`, `contentPadding: PaddingValues` | conteúdo, carregando, vazio, erro | itens ≥48dp; estados vazio/erro com heading | populada; carregando; vazia; erro; fontScale 2x |
| BookCard | Card de livro/coleção | `title`, `count: Int`, `onClick` | vazio | leitura por leitor de tela com contagem | normal; vazio |
| PortionStepper | Ajuste de porções | `value: Int`, `onChange(Int)`, `range: IntRange` | min/max, inválido | role=adjustable; announce mudanças | 1, meio, max; fontScale 2.0 |
| PrepBar | Mini‑timer persistente | `title`, `remaining: Duration`, `running: Boolean`, `onToggle()`, `onNextStep()`, `onAddTime(Duration)`, `onOpenPrep()` | running/paused/finished | announce tempo/restante e estado; não renderizar na RecipePrepScreen | running; paused; finished |
| ExternalVideoCTA | Ação “Abrir vídeo externo” | `platform: Platform`, `timestamp: Long`, `onClick` | indisponível | label com plataforma e tempo | disponível; indisponível |
 
| EmptyState | Vazio para listas/telas | `illustration`, `title`, `message`, `primaryAction` | — | elementos com rótulos | Home; Buscar; Livros |
| ErrorState | Exibir erro e ação | `message`, `onRetry` | — | contraste/ícone | genérico; específico |
| Tabs (genérico) | Abas para navegação contextual (Detalhe e outros) | `items: List<TabItem(label, icon?, badge?)`, `selectedIndex: Int`, `onSelect(Int)`, `scrollable: Boolean=false`, `style: TabStyle=Primary` | fixa/rolável; com/sem badge | role=Tab; foco por teclado; indicador visível | 3 itens; 5+ rolável; fontScale 2x |
| ReorderableList | Lista com arrastar p/ ordenar | `items`, `onMove(from,to)`, `key` | arrastando/solto | alça com hit ≥48dp; ações acessíveis | ingredientes; instruções |
| StepRichText | Renderiza passos com destaques | `text: String`, `entities: StepEntities`, `onIngredientTap(IngredientRef)`, `onTimerTap(Duration)` | sem entidades | spans com semantics clicáveis | sem; com ingrediente; com timer; com temperatura |
| IngredientTooltip | Tooltip de ingrediente | `anchorRect`, `content: IngredientInfo`, `onCopy()`, `onDismiss()` | — | role=dialog; foco inicial | padrão |
| IngredientRow | Linha de ingrediente com destaque | `text: String`, `quantityRange: IntRange?` | sem range | `AnnotatedString` com bold no range | com/sem destaque |
| FilterChips | Chips de filtro/busca | `filters`, `onToggle` | selected/unselected | tamanho ≥48dp | estados selecionado/não |
| CookzyModalBottomSheet | Sheet base com título e ações | `title: String`, `sheetState: SheetState`, `onDismissRequest`, `secondaryButton?`, `primaryButton?`, `content` | com/sem footer | announces sheet; foco e botões acessíveis; footer vertical (full‑width) | aberto; com/sem footer |
| LongOperationOverlay | Overlay bloqueante p/ operações longas | `visible: Boolean`, `message: String` | visível/oculto | live region + progressbar | visível/oculto |
| LongOperationDialog | Dialog de tela cheia p/ operações longas | `visible: Boolean`, `message: String` | visível/oculto | focus trap, não dismissível | visível/oculto |

Observação: respeitar tokens de `Theme.kt`, `Color.kt`, `Type.kt` e `ExtendedColorScheme`.

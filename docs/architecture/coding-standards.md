# Coding Standards — Cookzy (Android + Compose)

Escopo: padrões de implementação para o app Android do Cookzy. Estes padrões são lidos por agentes e humanos. Mantêm alinhamento com docs/architecture.md, AGENTS.md (Iconography Standard) e as regras do agente dev (BMAD).

## Princípios Gerais
- Composability primeiro: componentes pequenos, stateless quando possível; estado elevado para o nível apropriado.
- Camadas claras: UI/feature ↔ ViewModel ↔ domain ↔ data.
- Navegação tipada com `kotlinx.serialization`; nada de strings soltas de rota.
- DI com Koin; dependências e argumentos de navegação via parâmetros de Koin.
- Acessibilidade e semântica desde o início (contentDescription, roles, foco previsível).
- Strings de UI em `app/src/main/res/values/strings.xml` e consumidas com `stringResource`.
- Previews obrigatórias para telas e componentes em estados relevantes.

## Kotlin & Organização
- Nomes: `PascalCase` para classes/objetos; `camelCase` para funções/variáveis; constantes `UPPER_SNAKE`.
- Pacotes por domínio/feature: `feature/<nome>`, `ui`, `navigation`, `di`, `core`, `domain`, `data`.
- Evite classes “god”; extraia helpers privados e extensões.
- Preferir `sealed interface/class` para estados de UI quando fizer sentido.

## Padrões Compose
- Separar `Route` (injeção/navegação) de `Screen` (UI pura).
- State hoisting: telas recebem `state` e callbacks do ViewModel; evite lógica de negócio em Composables.
- Semântica: forneça `contentDescription`, `stateDescription` e roles quando aplicável.
- Tamanhos mínimos de toque ≥ 48dp; espaçamento base 8dp; respeitar `MaterialTheme`.
- Performance: use `remember`, `derivedStateOf` e chaves estáveis em listas; evite overdraw e sombras excessivas.

## ViewModel, Estado e Efeitos
- Basear em `BaseViewModel<State, Effect>` usando `StateFlow` imutável e canal de efeitos one‑shot.
- Um ViewModel por feature/tela.
- Coleta em UI via `collectAsStateWithLifecycle` e `LaunchedEffect` para efeitos.
- Helpers esperados: `updateState {}`, `sendEffect(effect)`.

## Navegação (Navigation Compose)
- Rotas tipadas serializáveis (ex.: `RecipeListDestination`, `RecipeEditorDestination`, `RecipeDetailDestination`).
- Profundidade de links: base `cookzy://recipes` com sufixos (`/editor`, `/detail`, `/prep`).
- Preservar estado ao navegar e ao dar pop; evite strings de rota hardcoded.

Exemplo (conceito):
```kotlin
@Serializable data class RecipeDetailDestination(val recipeId: Long)
val deepLinks = listOf(navDeepLink<RecipeDetailDestination>(basePath = "cookzy://recipes/detail"))
```

## Injeção de Dependências (Koin)
- Módulos em `app/src/main/java/.../di/AppModules.kt` (database, repository, viewModels).
- Obtenção em telas com `koinViewModel<T>(parameters = { parametersOf(args...) })`.
- Passe argumentos de navegação para o ViewModel via DI (parâmetros Koin), não via `SavedStateHandle`.

Exemplo (conceito):
```kotlin
val vm = koinViewModel<RecipeDetailViewModel>(parameters = { parametersOf(recipeId) })
```

## Tema, Tokens e Tipografia
- Usar `AppTheme` do módulo `:ds` com `Color.kt`, `Type.kt` e `Theme.kt`.
- `ExtendedColorScheme` para famílias adicionais (ex.: sucesso).
- Tipografia via Google Fonts Provider com fallback.

## Iconografia (IconRegistry)
- Biblioteca: Material Icons Extended (Compose BOM).
- Fonte única: todo ícone deve ser consumido via `IconRegistry` em `ds/.../icons/IconRegistry.kt`.
- Não mapear conceitos de domínio para ícones; defina propriedades por ícone.

Uso:
```kotlin
Icon(imageVector = IconRegistry.Close, contentDescription = stringResource(R.string.common_close))
```

## Previews
- Convenções: `Preview<Component>_<State>`; envolver com `AppTheme`.
- Utilize anotação combinada (ex.: `@ThemePreviews`) e wrapper utilitário conforme docs/architecture.md.
- Telas: crie prévias para vazio/carregando/erro/sucesso conforme aplicável.
- Inclua pelo menos uma variação `fontScale` ~2.0 e modo escuro.

## Testes
- Unit: JUnit + Coroutines Test + MockK; evite fakes caseiros.
- Room: use `room-testing` quando necessário; preferir repositórios isolados em testes da UI.
- Diretrizes: teste estados relevantes, efeitos one‑shot e navegação derivada.

## Qualidade & Observabilidade
- Logs de debug em `.ai/debug-log.md` quando executado por agente.
- Métricas de recomposição e performance podem ser avaliadas com ferramentas do Android Studio.

## Conformidade BMAD
- Koin para DI; atualize módulos quando adicionar serviços/ViewModels.
- Argumentos de navegação em construtores de ViewModel via DI.
- Strings via `strings.xml` (nada hardcoded em UI).
- Previews para cada tela/componente relevante com variações de estado.

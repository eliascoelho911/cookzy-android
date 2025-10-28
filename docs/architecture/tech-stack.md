# Tech Stack — Cookzy (Android)

Fonte de verdade: alinhado a `docs/architecture.md` e aos `build.gradle.kts` dos módulos `:app` e `:ds`.

## Linguagem, SDK e Build
- Linguagem: Kotlin (JVM target 11)
- Android: minSdk 24, target/compileSdk 36
- Compose ativado em `:app` e `:ds`

## UI
- Jetpack Compose + Material 3 (Compose BOM)
- Material Icons Extended via Compose BOM
- Google Fonts Provider (tipografia)

## Navegação
- Navigation Compose
- Rotas tipadas com `kotlinx.serialization`
- Deep links base: `cookzy://recipes` (sufixos `/editor`, `/detail`)

## DI
- Koin para Android + Compose + Navigation
- Padrão: `koinViewModel<VM>(parameters = { parametersOf(args...) })`

## Concor­rência
- Kotlin Coroutines + Flow

## Persistência
- Room (runtime, ktx, compiler via KSP)

## Lifecycle
- AndroidX Lifecycle (runtime-ktx, viewmodel-ktx, runtime-compose)

## Testes
- Unit: JUnit, Coroutines Test, MockK
- Robolectric (teste de unidade com dependências Android quando necessário)
- AndroidX Test (JUnit, Espresso)
- Compose UI Test (junit4, tooling, manifest para debug)
- Room Testing

## Módulos do Projeto
- `:app`
  - App host (Activity), NavHost, features, DI, data/domain.
  - Depende de `:ds` para tema e componentes de design system.
- `:ds`
  - Design system: tema, cores, tipografia, componentes compartilhados, prévias utilitárias e `IconRegistry`.

## Convenções de Integração
- Strings de UI em `app/src/main/res/values/strings.xml` (usar `stringResource`).
- Ícones via `IconRegistry` central (nada de ícones ad‑hoc).
- Argumentos de navegação nos construtores de ViewModel via Koin (não usar `SavedStateHandle` para isso).

## Referências de Código
- App: `app/src/main/java/com/eliascoelho911/cookzy/ui/CookzyApp.kt:1`, `app/src/main/java/com/eliascoelho911/cookzy/ui/CookzyNavHost.kt:1`
- Navegação: `app/src/main/java/com/eliascoelho911/cookzy/navigation/CookzyDestinations.kt:1`
- DI: `app/src/main/java/com/eliascoelho911/cookzy/di/AppModules.kt:1`
- DS: `ds/src/main/java/com/eliascoelho911/cookzy/ds/theme/Theme.kt:1`, `ds/src/main/java/com/eliascoelho911/cookzy/ds/icons/IconRegistry.kt:1`

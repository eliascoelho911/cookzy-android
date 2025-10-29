# Source Tree — Cookzy

Mapa de diretórios e arquivos principais do projeto para facilitar navegação e handoff.

## Raiz do Repositório
- `app/` — módulo aplicativo Android (features, navegação, DI, data/domain, UI host)
- `ds/` — design system (tema, cores, tipografia, componentes, ícones, prévias)
- `docs/` — documentação (arquitetura, PRD, QA, histórias)
- `design/` — artefatos de design (quando houver)

## Módulo :app (paths principais)
- `app/src/main/java/com/eliascoelho911/cookzy/android/`
  - `CookzyApplication.kt:1`, `MainActivity.kt:1`
- `app/src/main/java/com/eliascoelho911/cookzy/ui/`
  - `CookzyApp.kt:1`, `CookzyNavHost.kt:1`
- `app/src/main/java/com/eliascoelho911/cookzy/navigation/`
  - `CookzyDestinations.kt:1` (rotas tipadas + deep links)
- `app/src/main/java/com/eliascoelho911/cookzy/di/`
  - `AppModules.kt:1` (módulos Koin)
- `app/src/main/java/com/eliascoelho911/cookzy/core/`
  - `BaseViewModel.kt:1`
- `app/src/main/java/com/eliascoelho911/cookzy/feature/`
  - `recipelist/` — `RecipeListScreen.kt`, `RecipeListViewModel.kt`, `PreviewData.kt`
  - `recipedetail/` — `RecipeDetailScreen.kt`, `RecipeDetailViewModel.kt`, `PreviewData.kt`
  - `recipeeditor/` — `RecipeEditorScreen.kt`, `RecipeEditorViewModel.kt`, `PreviewData.kt`
- `app/src/main/java/com/eliascoelho911/cookzy/domain/`
  - `model/` — modelos de domínio (ex.: `Recipe.kt:1`)
  - `repository/` — portas de repositórios (ex.: `RecipeRepository.kt:1`)
  - `util/` — utilidades de domínio (ex.: `QuantityDerivation.kt:1`)
- `app/src/main/java/com/eliascoelho911/cookzy/data/`
  - `local/dao/` — DAOs Room (ex.: `RecipeDao.kt:1`)
  - `local/database/` — banco (`CookzyDatabase.kt:1`)
  - `local/entity/` — entidades Room
  - `local/model/` — agregados/joins para queries (`RecipeWithDetails.kt:1`)
  - `repository/` — implementações (ex.: `OfflineRecipeRepository.kt:1`)
- `app/src/main/res/`
  - `values/strings.xml:1` — strings de UI (fonte única)

## Módulo :ds (Design System)
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/theme/`
  - `Theme.kt:1`, `Color.kt:1`, `Type.kt:1`
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/components/`
  - componentes compartilhados (ex.: `CookzyModalBottomSheet.kt:1`)
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/icons/`
  - `IconRegistry.kt:1` — registro central de ícones
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/preview/`
  - utilitários e anotações de Preview

## Documentação (docs)
- `docs/architecture.md:1` — visão geral de arquitetura
- `docs/architecture/`
  - `coding-standards.md:1` — padrões de código e UI
  - `tech-stack.md:1` — tecnologias e bibliotecas
  - `source-tree.md:1` — este mapa de diretórios
- `docs/prd.md:1` — PRD
- `docs/qa/` — materiais de QA
- `docs/stories/` — histórias de desenvolvimento

## Convenções Relevantes
- Ícones: sempre via `IconRegistry` (Material Icons Extended; nada de ícones ad‑hoc).
- Navegação: destinos tipados; deep links em `CookzyDestinations.kt`.
- DI: Koin; parâmetros de navegação injetados em ViewModels via `parametersOf`.
- Previews: obrigatórias por componente/tela em estados principais; usar wrapper de tema.

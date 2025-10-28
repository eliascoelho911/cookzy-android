# Cookzy — Front-end Architecture (Android Compose)

## Change Log

| Date       | Version | Description                                | Author     |
|------------|---------|--------------------------------------------|------------|
| 2025-10-22 | 0.1     | Documento criado (esqueleto inicial)       | Sally (UX) |
| 2025-10-22 | 0.2     | Inventário de componentes e padrões de preview | Sally (UX) |

## Visão Geral

Este documento descreve como implementar a UI do Cookzy usando Jetpack Compose, Navigation Compose e Koin, alinhado à Especificação de UI/UX (docs/front-end-spec.md). Foca em padrões de navegação, gerenciamento de estado, injeção de dependências, tema e diretrizes de testes.

Para evitar sobreposição e manter uma única fonte de verdade para tópicos específicos, consulte:
- Tech Stack: `docs/architecture/tech-stack.md`
- Source Tree: `docs/architecture/source-tree.md`
- Coding Standards: `docs/architecture/coding-standards.md`

## Stack de Front-end

Para o stack completo e sempre atualizado, veja `docs/architecture/tech-stack.md`.

## Estrutura do Projeto (UI)

Para o mapa de diretórios e caminhos principais, veja `docs/architecture/source-tree.md`.

## Navegação

Para padrões, exemplos e boas práticas de navegação tipada, consulte `docs/architecture/coding-standards.md` (seções Navegação e Composição de UI). As rotas e deep links efetivos residem no código: `app/src/main/java/com/eliascoelho911/cookzy/navigation/CookzyDestinations.kt:1`.

## Injeção de Dependências (Koin)

Diretrizes de DI e exemplos: ver `docs/architecture/coding-standards.md` (seção Injeção de Dependências). Os módulos vivem em `app/src/main/java/com/eliascoelho911/cookzy/di/AppModules.kt:1`.

## Gerenciamento de Estado

Padrões de ViewModel, estado e efeitos: ver `docs/architecture/coding-standards.md` (seção ViewModel, Estado e Efeitos). Implementação base em `app/src/main/java/com/eliascoelho911/cookzy/core/BaseViewModel.kt:1`.

## Composição de UI

Padrões de composição, semântica e convenções de Route vs Screen: ver `docs/architecture/coding-standards.md` (seções Compose e Previews) e o módulo `:ds` para componentes compartilhados.

## Tema & Tokens

Diretrizes de tema e tipografia: ver `docs/architecture/coding-standards.md` (seção Tema, Tokens e Tipografia).
Os valores de tokens são definidos no Design System e não são duplicados aqui. Consulte:
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/theme/Color.kt:1`
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/theme/Type.kt:1`
- `ds/src/main/java/com/eliascoelho911/cookzy/ds/theme/Theme.kt:1`

## Responsividade

- Seguir a seção “Responsividade” da spec (docs/front-end-spec.md)
- Adotar `WindowSizeClass` quando introduzir variações (≥ 840 dp multipainel futuramente)
- Sheets com largura máx. ~640 dp em telas largas

## Acessibilidade

Diretrizes de acessibilidade e semântica estão em `docs/architecture/coding-standards.md` (Compose Semantics e A11y). Objetivo: conformidade WCAG 2.2 AA e boas práticas Android.

## Animações & Microinterações

- Usar APIs Compose (AnimatedContent/animate*) e curvas Material
- Respeitar flag de “reduzir animações”; oferecer alternativas (fade/instant)
- Timings sugeridos na spec (tabs, sheets, barra de preparo, stepper)

## Testes

Arquitetura de testes e ferramentas recomendadas: ver `docs/architecture/coding-standards.md` (seção Testes). Exemplos específicos podem residir em `app/src/test/...`.

## Performance

Orientações de performance (recomposições, chaves estáveis, overdraw) estão em `docs/architecture/coding-standards.md`.

## Inventário de Componentes (Compose)

Para a versão canônica e sempre atualizada da tabela de componentes, consulte `docs/architecture/components-inventory.md`.

## Iconografia & IconRegistry

Padrão de ícones e consumo único via `IconRegistry`: ver `docs/architecture/coding-standards.md` (seção Iconografia) e o “Iconography Standard” em `AGENTS.md`. Implementação do registro em `ds/src/main/java/com/eliascoelho911/cookzy/ds/icons/IconRegistry.kt:1`.

## Padrões de Preview (Compose)

Convenções de prévias (anotações, wrappers, variações de estado/tema/A11y): ver `docs/architecture/coding-standards.md` (seção Previews) e utilitários no módulo `:ds`.

## Referências

- Especificação de UI/UX: docs/front-end-spec.md
- PRD: docs/prd.md

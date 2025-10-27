# Compose Prototypes

## Objetivo
O módulo `prototype` concentra protótipos Jetpack Compose que funcionam como a única fonte de design antes do desenvolvimento de produção. Cada tela do PRD/front-end-spec deve ter uma versão `@Preview` aqui, com estados relevantes e dados simulados.

## Estrutura do Módulo
- Caminho base: `prototype/src/main/java/com/eliascoelho911/cookzy/prototype`
- Agrupe arquivos por domínio de funcionalidade (ex.: `home/`, `recipe/detail/`, `recipe/editor/`)
- Para dados simulados, use objetos em `prototype/src/main/java/com/eliascoelho911/cookzy/prototype/data`
- Utilize temas e estilos compartilhados do módulo `ui` para consistência visual.

## Convenções de Preview
- Nomes: `Preview<ScreenName><State>` (ex.: `PreviewRecipeDetailLoading`)
- Anotações: sempre use `@Preview(showBackground = true, widthDp = ..., heightDp = ...)` para reforçar tamanhos principais
- Dados fake: disponibilize via helpers (`PreviewRecipeData.sampleDetail()`) e evite acessar fontes reais
- Sem navegação real: substitua callbacks por lambdas vazias (`onBack = {}`) e não dependa de ViewModels efetivos

## Checklist de Entrega
1. Adicionar ao menos um `@Preview` por estado relevante descrito na front-end-spec
2. Atualizar o catálogo abaixo com o novo preview e link para o arquivo
3. Seções da front-end-spec → “Compose Prototypes” devem refletir telas/estados adicionados
4. Registrar decisões ou limitações temporárias na coluna “Notas” do catálogo

## Catálogo de Previews
| Tela | Estado(s) coberto(s) | Arquivo | Notas |
|------|----------------------|---------|-------|
| _Preencher_ | _Preencher_ | _Preencher_ | _Pendências, TODOs, etc._ |


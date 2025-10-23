# Sprint Change Proposal — Alinhamento Epic 01 com ajustes do PRD (2025-10-23)

Status: RASCUNHO PARA REVISÃO/APPROVAÇÃO DO USUÁRIO

---

## 1) Contexto e Gatilho

- Gatilho: Ajustes recentes no PRD (docs/prd.md) impactam o Épico 01 e histórias associadas (ex.: troca de “Tags” por “Livros de Receitas”, Importação MVP focada em YouTube com revisão no Editor, vídeo externo via Intent, inclusão da “Barra de Preparo”, Stepper de porções inline no Detalhe).
- Story gatilho principal: Story 1.2 (Visualizar Receita com abas) — atualmente menciona “quantidades/unidades”, o que conflita com o novo modelo de dados baseado apenas em `rawText` (com quantidade derivada on‑demand, sem unidade persistida) definido e implementado na Story 1.1.
- Objetivo: Eliminar inconsistências e reduzir retrabalho, alinhando épico e histórias ao PRD e ao modelo de dados vigente.

## 2) Checklist — Mudança: Itens Principais (Seções 1–4)

- [x] Identificar Story gatilho e definir o problema: conflito entre ACs/tarefas e o modelo `rawText` (sem unidade persistida).
- [x] Impacto inicial: risco de implementação divergente; confusão em testes/QA; referências de documentação quebradas.
- [x] Análise de Épico atual: pode ser mantido sem mudanças de escopo; requer apenas ajustes de dependências/referências.
- [x] Análise de Artefatos: atualizar Stories 1.2 e 1.3; corrigir referência de arquitetura no Épico 01; manter Stepper/PrepBar/CTA vídeo em épicos futuros correspondentes.
- [x] Opções de caminho: 1) Ajuste direto (recomendado); 2) Rollback (não aplicável); 3) Reescopo MVP (não necessário neste momento).
- [x] Caminho recomendado: Ajuste direto/incremental nos artefatos afetados.

## 3) Resumo de Impacto em Artefatos

- PRD (docs/prd.md): já refletindo as decisões — sem alteração adicional agora para o Épico 01.
- Épico 01 (docs/epics/01-crud-de-receitas.md): corrigir referência de documentação de arquitetura para `docs/ui-architecture.md`.
- Story 1.2 (docs/stories/1.2.visualizar-receita-com-abas.md): alinhar ACs e tarefas para ingredientes baseados em `rawText`, sem unidade persistida; atualizar Dev Notes para `docs/ui-architecture.md`.
- Story 1.3 (docs/stories/1.3.excluir-receita-com-confirmacao-e-undo.md): atualizar Dev Notes para `docs/ui-architecture.md`.

Impactos adicionais vindos dos novos wireframes (design/wireframes)
- RecipeList-wireframe.md: carrossel “Recentes” (1 card/viewport), carrossel de “Livros” (filtro), busca inline, alternância lista↔grade.
  - Proposta para Epic 01: implementar “Recentes”, lista/grade e busca inline; adiar carrossel de “Livros” para o épico de Coleções.
- RecipeEditor-wireframe.md: Stepper de Porções, Card “Nutrição”, “Livros de Receitas”, “Link de origem”.
  - Proposta para Epic 01: manter campos opcionais como PLACEHOLDER ou ocultos — Stepper de Porções e Nutrição entram no épico de Cálculos/Nutrição; “Livros” no épico de Coleções; “Link de origem” no épico de Importação.
- RecipeDetail-wireframe.md: Hero com CTA de vídeo, Resumo Nutritivo, “Iniciar preparo”, Stepper de Porções, Prep Bar persistente, chips de tempo/temperatura.
  - Proposta para Epic 01: mostrar apenas abas Ingredientes/Preparo/Nutrição simples; ocultar Resumo Nutritivo/Stepper/Prep Bar/CTA de vídeo/chips até os épicos correspondentes (Preparo, Importação, Cálculos/Nutrição).

Itens do PRD NÃO trazidos para o Épico 01 agora (confirmado pelo usuário):
- Stepper de porções inline → ficará em épico futuro de Cálculos/Nutrição.
- Barra de Preparo (mini‑timer persistente) → ficará no épico de Preparo.
- CTA de vídeo externo (timestamp) → ficará no épico de Importação/Preparo.

## 4) Propostas de Edição (precisas e aplicáveis)

As mudanças abaixo são propostas textuais para aplicar após aprovação. Trechos “antes → depois”.

1. docs/epics/01-crud-de-receitas.md
   - Seção “Dependências”
     - Antes: `Arquitetura de ViewModels e contratos de estado/evento (docs/front-end/03-arquitetura-componentes.md).`
     - Depois: `Arquitetura de ViewModels e contratos de estado/evento/efeito (docs/ui-architecture.md).`

2. docs/stories/1.2.visualizar-receita-com-abas.md
   - Seção “Tasks / Subtasks” > “Tela Detalhe…”
     - Antes: `Ingredientes: lista com quantidades/unidades (AC: 1)`
     - Depois: `Ingredientes: exibir cada item a partir de rawText (uma linha por ingrediente) e destacar a quantidade derivada quando inferível (sem unidade persistida) (AC: 1)`
   - Seção “Dev Notes”
     - Antes: `Ver docs/front-end/03-arquitetura-componentes.md para contratos de Estado/Evento/Efeito e RecipeDetailViewModel.`
     - Depois: `Ver docs/ui-architecture.md para contratos de Estado/Evento/Efeito e RecipeDetailViewModel.`
   - Seção “Testing” (complemento)
     - Adicionar: `- Testes de UI: verificar destaque de quantidade derivada quando inferível e ausência de unidade persistida.`

   - Seção “Acceptance Criteria” (complementos/ajustes)
     - Adicionar: `- Stepper de porções NÃO incluído neste escopo; valores e recalculados pertencem a épico futuro (Cálculos/Nutrição).`
     - Adicionar: `- CTA de vídeo externo e Prep Bar NÃO incluídos neste escopo; pertencem aos épicos de Importação/Preparo.`
     - Adicionar: `- Aba Nutrição pode exibir placeholder quando não houver dados.`

3. docs/stories/1.3.excluir-receita-com-confirmacao-e-undo.md
   - Seção “Dev Notes”
     - Antes: `docs/front-end/02-interacoes-e-estados.md (feedbacks visuais e mensagens).`
     - Depois: `docs/ui-architecture.md (seções de Interações/Estados e padrões de feedback).`

4. docs/epics/01-crud-de-receitas.md
   - Seção “Stories sugeridas” (acréscimo)
     - Adicionar item `5. Lista de receitas (Home): carrossel de recentes (1 por viewport), alternância lista/grade, busca inline; sem carrossel de livros no Épico 01.`
   - Seção “Escopo” (nota)
     - Adicionar: `- Itens como Stepper de porções, Resumo Nutritivo, Prep Bar e CTA de vídeo externo são tratados em épicos dedicados (Cálculos/Nutrição, Preparo, Importação) e NÃO fazem parte do Épico 01.`

## 5) MVP/Objetivos — Impacto

- MVP do PRD permanece consistente. Ajustes são de alinhamento documental/processual, sem mudar escopo do Épico 01.

## 6) Plano de Ação (alto nível)

1) Aprovar este Sprint Change Proposal.  
2) Aplicar as edições nos arquivos listados (PR recomendado).  
3) PO/SM: atualizar backlog para épicos futuros com Stepper de porções, PrepBar e CTA de vídeo externo.  
4) Dev: implementar Story 1.2 conforme ACs/Notações atualizadas.  
5) QA: ajustar cenários e critérios de verificação (quantidade derivada sem unidade; estados padrão; integração com persistência).

## 7) Handoff e Responsáveis

- PM: coordenação desta proposta e aprovação final.  
- PO: backlog/story refinements adicionais se necessário.  
- SM: sequencing/planejamento de sprint.  
- Dev: ajustes na Story 1.2.  
- QA: casos de teste atualizados para 1.2.

---

Confirme “APROVADO” para que eu aplique as edições nos arquivos originais ou indique ajustes desejados neste documento.

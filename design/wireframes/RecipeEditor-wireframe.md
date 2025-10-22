# Recipe Editor — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Fluxo Criar Receita, Wireframes & Mockups) e docs/ui-architecture.md.

Objetivo
- Criar/editar receitas com título, ingredientes e passos; salvar com feedback e evitar perda acidental.

Diretriz de Layout
- Baseado no print compartilhado (app bar com “check” para salvar e seções Título/Imagem/Informações/Nutrição), mantendo os requisitos funcionais mínimos e a aderência ao Material 3 definidos na spec.

Elementos‑chave
- App Bar com voltar e ação de salvar: “Nova receita” | “Editar receita” + ícone ✔ para salvar (desabilitado até cumprir requisitos mínimos).
- Sessões apresentadas como Cards: “Título”, “Imagem”, “Informações”, “Nutrição”, “Ingredientes”, “Instruções”.
- Cards “Ingredientes” e “Instruções” suportam reordenar por arrastar (alça “≡”), além de adicionar/remover.
- Validações inline com mensagens claras; feedback por Snackbar para erros gerais (ex.: falha ao salvar).

Wireframe (Mobile)

```
┌──────────────────── App Bar ─────────────────────┐
│ ←  Nova receita                          [   ✔ ] │  ← ✔ = Salvar
└──────────────────────────────────────────────────┘

  ┌────────────── Card: Header ───────────────┐
  [ Título da receita ]
  [___________________________________________]     ← erro abaixo quando inválido
  
  Imagem                                  [📷]
  (thumbnail vazio • botão câmera em container 44–48dp)

  Informações                       2 porções  ›  ← abre sheet/página (porções, tempo, tags)
  
  Nutrição                                   ›  ← opcional; abre nutrição por porção
  └────────────────────────────────────────────┘

  ┌──────────── Card: Ingredientes ───────────┐
  INGREDIENTES
  +  Adicionar ingrediente ou seção            ← lista nasce vazia

  (itens renderizados abaixo quando houver)
  •  ≡  [Texto do ingrediente …]          [✕]
  •  ≡  [Texto do ingrediente …]          [✕]
        ↑ arrastar pela alça “≡” para reordenar
  └────────────────────────────────────────────┘

  ┌──────────── Card: Instruções ─────────────┐
  INSTRUÇÕES
  +  Adicionar passo ou seção

  (itens renderizados abaixo quando houver)
  1) ≡  [Texto do passo …]
         [quebra de linha opcional]
         [✕ Remover]
        ↑ arrastar pela alça “≡” para reordenar
  └────────────────────────────────────────────┘

  Snackbar (erros) aparece na base quando necessário
```

Requisitos Mínimos (conforme spec)
- Salvar habilita somente com: Título preenchido + ≥ 1 ingrediente + ≥ 1 passo.
- Cards “Imagem”, “Informações” e “Nutrição” são opcionais e não bloqueiam o salvar no MVP.

Interações
- Voltar (←):
  - Sem alterações: sai imediatamente.
  - Com alterações: confirmar descarte OU aplicar estratégia de rascunho/autosave (conforme docs/front-end-spec.md).
- Salvar (✔): valida campos mínimos; exibe loading no ícone/badge; ao sucesso, navega para Detalhe da Receita.
- Imagem: solicita permissão sob demanda; abre seletor/câmera; mostra preview/estado de carregamento; permite remover.
- Informações: abre sheet/página para “Porções (inteiro 1–99)”, “Tempo (min)”, “Tags” — todos opcionais.
- Nutrição: abre página para resumo nutricional por porção (opcional/placeholder); pode ser configurado depois da criação.
- Ingredientes/Instruções: “+ Adicionar …” foca imediatamente no novo item; remover com [✕] respeitando mínimo; reordenar por arrastar a alça “≡”.

Validações & Mensagens
- Título obrigatório: mensagem abaixo do campo (“Informe um título”).
- Ingredientes/Instruções: destacar a seção faltante com texto de ajuda (“Adicione pelo menos um ingrediente/passo”).
- Erros gerais (IO/rede): Snackbar com ação “Tentar de novo”.

Estados
- Novo: campos vazios; ✔ desabilitado até cumprir mínimos.
- Edição: populações existentes; ✔ habilita quando estado atual atende mínimos; loading substitui ✔ durante operação.
- Falha ao salvar: manter dados e foco; Snackbar + ícone de erro transitório na App Bar.

Acessibilidade
- Alvos ≥ 48dp; labels claros; ordem de foco natural.
- Ícone de salvar com `contentDescription` (“Salvar receita”).
- Cards “Imagem/Informações/Nutrição” com role=button e `stateDescription` quando houver valor (ex.: “2 porções”).
- Erros vinculados aos campos via semantics; leitura de Snackbar com prioridade apropriada.
- Reordenação acessível: além do arrastar, oferecer no item ações “Mover para cima/baixo” e “Mover para posição…”. Anunciar “Movido para posição N”.

Responsividade
- MVP 1 coluna; em ≥ 840 dp manter layout de coluna única (sem multipainel nesta versão).

Notas para Dev
- Arquivo base: app/src/main/java/.../recipeeditor/RecipeEditorScreen.kt
- Adicionar previews: novo; edição; salvando (com indicadores); erros de validação.
- Desabilitar ✔ via estado derivado dos mínimos; exibir `CircularProgressIndicator` pequeno no lugar do ícone durante salvar.
 - Listas reordenáveis (ingredientes/instruções): usar alça “≡”, chaves estáveis, auto‑scroll ao arrastar e feedback háptico ao soltar.
 - Acessibilidade de reordenação: incluir ações “Mover para cima/baixo” no menu do item e anunciar posição final.

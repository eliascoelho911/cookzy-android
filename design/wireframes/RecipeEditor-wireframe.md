# Recipe Editor — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Fluxo Criar Receita, Wireframes & Mockups) e docs/ui-architecture.md.

Objetivo
- Criar/editar receitas com título, ingredientes e passos; salvar com feedback e evitar perda acidental.

Elementos‑chave
- App Bar com título contextual: “Nova receita” | “Editar receita” + ação “Cancelar”.
- Campos: Título (single line), Ingredientes (lista), Passos (lista).
- Ações: Adicionar Ingrediente/Passo; Remover item; Salvar; Descartar alterações.
- Validações inline com mensagens claras.
- Snackbar para erros gerais (ex.: falha ao salvar).

Wireframe (Mobile)

```
┌──────────────── App Bar ─────────────────┐
│  Nova receita                    [Cancelar]│
└───────────────────────────────────────────┘

  Título
  [_____________________________] (erro?)    ← mensagem abaixo quando inválido

  Ingredientes                [ + Adicionar ]
  1) [______________]      [ Remover ]
  2) [______________]      [ Remover ]
     … (min 1, pode remover quando >1)

  Passos                      [ + Adicionar ]
  1) [______________________________]
     [______________________________]
     [ Remover ]
  2) [______________________________]
     [ Remover ]

  [ Salvar ]
  [ Descartar alterações ]

  Snackbar (erros) aparece na base quando necessário
```

Estados
- Novo: campos vazios; botão Salvar habilita com requisitos mínimos.
- Edição: campos populados; ao salvar, mostrar loading no botão e desabilitar ações.
- Saving: indicador no botão [ Salvar ].

Interações
- Cancelar/Descartar: confirmar quando houver alterações não salvas (ou estratégia de rascunho/autosave conforme spec).
- Adicionar/Remover: mantém foco previsível e ordem de leitura; scroll preservado.
- Ao salvar: navegar para Detalhe da Receita recém‑criada/editada.

Acessibilidade
- Alvos ≥ 48dp; labels claros; ordem de foco natural.
- Erros anunciados com rótulos e ligação ao campo.

Responsividade
- MVP 1 coluna; em ≥ 840 dp, manter colunas simples (sem multipainel nesta versão).

Notas para Dev
- Arquivo base: app/src/main/java/.../recipeeditor/RecipeEditorScreen.kt
- Adicionar previews: novo; edição; salvando (com indicadores).


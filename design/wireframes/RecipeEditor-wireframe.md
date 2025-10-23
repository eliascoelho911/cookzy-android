# Recipe Editor — Wireframe (MVP)

Fonte: docs/front-end-spec.md (Fluxo Criar Receita, Wireframes & Mockups) e docs/ui-architecture.md.

Objetivo
- Criar/editar receitas com título, ingredientes e passos; salvar com feedback e evitar perda acidental.

Diretriz de Layout
- Baseado no print compartilhado (app bar com “check” para salvar e seções Cabeçalho/Nutrição/Ingredientes/Instruções), mantendo os requisitos funcionais mínimos e a aderência ao Material 3 definidos na spec.

Elementos‑chave
- App Bar com voltar e ação de salvar: “Nova receita” | “Editar receita” + ícone ✔ para salvar (desabilitado até cumprir requisitos mínimos).
- Sessões apresentadas como Cards: “Cabeçalho”, “Nutrição”, “Ingredientes”, “Instruções”. (Card “Nutrição” [Fora do escopo do Épico 01])
- Cards “Ingredientes” e “Instruções” suportam reordenar por arrastar (alça “≡”), além de adicionar/remover.
- Validações inline com mensagens claras; feedback por Snackbar para erros gerais (ex.: falha ao salvar).
 - Origem (quando houver importação): exibir “Link de origem” no Card Cabeçalho com ação “Abrir link”. [Fora do escopo do Épico 01]

## Escopo — Épico 01

- Inclui (neste épico): App Bar com salvar (gating), Cards “Cabeçalho/Ingredientes/Instruções”, adicionar/remover e reordenar itens, validações e Snackbar.
- Fora do escopo (outros épicos): Stepper de Porções; Card “Nutrição”; “Livros de Receitas”; “Link de origem”. Esses itens devem permanecer ocultos ou como placeholders não funcionais neste épico.

Wireframe (Mobile)

```
┌──────────────────── App Bar ─────────────────────┐
│ ←  Nova receita                          [   ✔ ] │  ← ✔ = Salvar
└──────────────────────────────────────────────────┘

  ┌───────────── Card: Cabeçalho ──────────────┐
  [ Título da receita ]
  [___________________________________________]     ← erro abaixo quando inválido

  Imagem                                  [📷]
  (thumbnail vazio • botão câmera em container 44–48dp)

  Porções                 [  −   2   +  ]        ← Stepper 1–99 (padrão 1)
  Tempo (min)             [      25      ]        ← numérico opcional
  Livros de Receitas      [ + adicionar livro de receitas ]     ← chips: [massa] [almoço] [veg]
  Link de origem (opcional)  https://youtu.be/abc123    [ Abrir link ]
  └────────────────────────────────────────────┘

  ┌───────────── Card: Nutrição ──────────────┐
  Nutrição                                   ›  ← opcional; abre sheet “Nutrição por porção”
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
- Card “Imagem” e Card “Nutrição” são opcionais e não bloqueiam o salvar no MVP. Campos “Porções/Tempo/Livros de Receitas” são opcionais (padrões: porções=1; tempo vazio; sem livros de receitas).

Interações
- Voltar (←):
  - Sem alterações: sai imediatamente.
  - Com alterações: confirmar descarte OU aplicar estratégia de rascunho/autosave (conforme docs/front-end-spec.md).
- Salvar (✔): valida campos mínimos; exibe loading no ícone/badge; ao sucesso, navega para Detalhe da Receita.
- Imagem: solicita permissão sob demanda; abre seletor/câmera; mostra preview/estado de carregamento; permite remover.
- Porções (no Card Cabeçalho): Stepper 1–99; anuncia mudanças; persiste valor para a receita.
- Tempo (min): campo numérico (0–999); formatação e validação simples; opcional.
- Livros de Receitas: campo de entrada com chips adicionáveis/removíveis; sugestões/autocomplete opcional; limite suave de 8 livros de receitas.
- Nutrição: abre sheet “Nutrição por porção” (opcional/placeholder); pode ser configurado depois da criação.
- Ingredientes/Instruções: “+ Adicionar …” foca imediatamente no novo item; remover com [✕] respeitando mínimo; reordenar por arrastar a alça “≡”.

Validações & Mensagens
- Título obrigatório: mensagem abaixo do campo (“Informe um título”).
- Ingredientes/Instruções: destacar a seção faltante com texto de ajuda (“Adicione pelo menos um ingrediente/passo”).
- Erros gerais (IO/rede): Snackbar com ação “Tentar de novo”.

Estados
- Novo: campos vazios; ✔ desabilitado até cumprir mínimos.
- Edição: populações existentes; ✔ habilita quando estado atual atende mínimos; loading substitui ✔ durante operação.
- Falha ao salvar: manter dados e foco; Snackbar + ícone de erro transitório na App Bar.

Variante — Revisar Importação
- Quando aberto a partir do fluxo de Importar, usar o mesmo Editor com título da App Bar “Revisar importação”.
- Exibir “Link de origem” no Card Cabeçalho (plataforma + URL) com ação “Abrir link”.
- Após salvar, navegar para o Detalhe da receita.

Acessibilidade
- Alvos ≥ 48dp; labels claros; ordem de foco natural.
- Ícone de salvar com `contentDescription` (“Salvar receita”).
- Card “Cabeçalho” com `role=group`; Stepper de Porções com `role=adjustable`; Tempo com teclado numérico e label claro; Livros de Receitas com chips acessíveis (remover/ativar por teclado).
- Card “Nutrição” com role=button e `stateDescription` quando houver valores preenchidos.
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
 - Origem: quando presente, renderizar no Card Cabeçalho um campo somente leitura com URL normalizada e botão “Abrir link” (Intent ACTION_VIEW). Persistir metadado de origem com a receita.

### Sheet Nutrição — Detalhes

Propósito: inserir/editar nutrição por porção. Opcional, sem bloqueio do salvar.

Padrão de Sheet
- Usar `CookzyModalBottomSheet` como base (título + IconButton fechar; corpo com campos; footer com botões Salvar/Limpar quando aplicável).

```
┌────────────── Bottom Sheet: Nutrição por porção ──────────────┐
│  Nutrição por porção                               [  Fechar ] │
│  (arrastar para baixar)                                        │
├────────────────────────────────────────────────────────────────┤
│ Calorias (kcal)        [     320     ]                          │
│ Carboidratos (g)       [      45     ]                          │
│ Proteínas (g)          [      12     ]                          │
│ Gorduras (g)           [      10     ]                          │
│ —————————————————  Campos opcionais  ————————————————          │
│ Fibra (g)              [       5     ]                          │
│ Açúcares (g)           [       8     ]                          │
│ Sódio (mg)             [      360    ]                          │
├────────────────────────────────────────────────────────────────┤
│  [                         Salvar                       ]      │
│  [                         Limpar                       ]      │
└────────────────────────────────────────────────────────────────┘
```

Regras e validações
- Unidades fixas por campo: kcal, g e mg conforme label.
- Valores inteiros/decimais positivos; aceitar vírgula como separador decimal; normalizar para ponto.
- “Salvar” grava no estado e fecha a sheet; “Limpar” zera todos os campos. Footer em coluna (full‑width), seguindo `CookzyModalBottomSheet`.
- Se todos os campos estiverem vazios, considerar “nutrição não informada”.

Acessibilidade (sheet)
- Foco inicial no primeiro campo; rótulos com unidades explícitas.
- Leitores anunciam “por porção”; botões com labels claros (“Salvar nutrição”, “Limpar campos”).
- Fechamento por gesto de arrastar/tocar fora/Back, com retorno de foco para o Card “Nutrição”.

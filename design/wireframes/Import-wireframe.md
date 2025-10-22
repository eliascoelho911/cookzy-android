# Importar Receita — Fluxo e Wireframes (MVP)

Fonte: docs/front-end-spec.md (FAB + Importar) • docs/epics/04-importacao-de-receitas.md • docs/ui-architecture.md.

Objetivo
- Permitir importar receitas de YouTube (MVP) colando um link, extrair dados mínimos, revisar/ajustar e salvar como receita local.

Visão Geral do Fluxo
1) FAB Sheet → Importar
2) Sheet “Importar” (origem + URL) → Extrair
3) Extraindo (tela cheia) → sucesso OU erro
4) Revisão no Editor (variante) → Salvar
5) Sucesso → navegar para Detalhe da Receita

Padrão de Sheet
- Usar `CookzyModalBottomSheet` (título + IconButton fechar, corpo rolável, footer opcional).

## 1) Sheet — Importar receita (origem + link)

```
┌──────────────────────── Bottom Sheet ──────────────────────────┐
│             ───                                                 │
│  Importar receita                                   [  ✕  ]     │
├────────────────────────────────────────────────────────────────┤
│ Origem                                                          │
│  (●) YouTube        (○) TikTok [desativado] (○) Instagram [off] │
│  (○) Link genérico [futuro]                                     │
│                                                                  │
│ URL do vídeo                                                    │
│ [ https://youtube.com/watch?v=dQw4w9WgXcQ            (  ⎘  ) ]  │
│                                                                  │
│ Dica: cole um link de YouTube. Ex.: https://youtu.be/abc123     │
├────────────────────────────────────────────────────────────────┤
│  [ Cancelar ]                                                   │
│  [   Extrair   ]                                                │
│   secundário                 primário                           │
└────────────────────────────────────────────────────────────────┘
```

Validações (sheet)
- Habilitar “Extrair” apenas com URL válida para a origem selecionada (regex YouTube).
- Mostrar mensagem de erro inline abaixo do campo quando inválido.
- Foco inicial no campo URL; teclado aberto.

## 2) Extraindo dados (tela cheia)

```
┌───────────────────── Tela Atual (Home/Lista) ─────────────────────┐
│                                                                    │
│   ────────────────────────── SCRIM ───────────────────────────     │
│   │                                                              │  │
│   │      [ ⟳ ]                                                   │  │
│   │      Importando receita…                                     │  │
│   │      (isso pode levar alguns segundos)                       │  │
│   │                                                              │  │
│   ──────────────────────────────────────────────────────────────     │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

- Exibir overlay de operação longa em tela cheia usando `LongOperationOverlay`.
- Ao iniciar a extração, fechar o sheet de Importar para reduzir camadas (ou mantê-lo atrás — overlay bloqueia interação; decisão opcional). 
- Overlay é bloqueante e não possui “Cancelar” no MVP (padrão pode suportar, mas desabilitado aqui).

## 3) Erro na extração (sheet)

```
┌──────────────────────── Bottom Sheet ──────────────────────────┐
│  Importar receita                                   [  ✕  ]     │
├────────────────────────────────────────────────────────────────┤
│ Não foi possível extrair dados deste link.                      │
│ • Verifique se o vídeo é público e o link está correto.         │
│ • Tente novamente mais tarde.                                   │
├────────────────────────────────────────────────────────────────┤
│  [ Editar manualmente ]                                         │
│  [  Tentar novamente  ]                                         │
└────────────────────────────────────────────────────────────────┘
```

Ações
- Tentar novamente: retorna ao estado do formulário com o último link preenchido e foco no botão “Extrair”.
- Editar manualmente: fecha o sheet e abre o Editor de receita vazio (ou com título pré-preenchido se disponível).

## 4) Revisão da Importação — no Editor

Objetivo
- Permitir revisar/ajustar dados extraídos e salvar a receita. Usa a MESMA tela do Editor (design/wireframes/RecipeEditor-wireframe.md) com a variante “Revisar importação”.

Elementos‑chave
- App Bar: “Revisar importação” com voltar e ação Salvar (✔).
- Card Cabeçalho (do Editor): inclui “Link de origem” (plataforma + URL) com ação “Abrir link”.
- Demais Cards do Editor: Cabeçalho (Título/Imagem/Porções/Tempo/Livros), Ingredientes (lista), Instruções (lista). Nutrição opcional.
- Feedback de validação mínimo igual ao Editor (título obrigatório, ≥1 ingrediente e ≥1 passo).

Wireframe (Mobile) — (mesmo Editor, com origem no Cabeçalho)

```
┌──────────────────── App Bar ─────────────────────┐
│ ←  Revisar importação                     [  ✔ ] │
└──────────────────────────────────────────────────┘

  ┌───────────── Card: Cabeçalho ────────────┐
  [ Título da receita extraído ]
  [_______________________________________]
  Origem: YouTube   [ Abrir link ]
  Link: https://youtu.be/abc123
  Porções  [  −  2  + ]    Tempo (min) [ 25 ]
  └──────────────────────────────────────────┘

  ┌──────────── Card: Ingredientes ──────────┐
  • **200 g** farinha de trigo           [✎]
  • **1** ovo                            [✎]
  + Adicionar ingrediente
  └──────────────────────────────────────────┘

  ┌──────────── Card: Instruções ────────────┐
  1) Preaqueça o forno a  🔥 180°C
  2) Misture a __farinha de trigo__          [✎]
  + Adicionar passo
  └──────────────────────────────────────────┘

  Snackbar de erro (quando falhar salvar)
```

Estados
- Carregando (ao abrir): opcional, exibir skeleton por 200–400 ms se necessário.
- Erro ao salvar: Snackbar com ação “Tentar de novo”.
- Sucesso: navegar para Detalhe da Receita.

Interações
- Voltar: se houve alterações não salvas, confirmar descarte (como Editor) ou manter rascunho (a definir).
- Salvar (✔): valida mínimos; ao processar, mostrar loading no lugar do ✔; sucesso → Detalhe.
- “Abrir link”: Intent ACTION_VIEW (fallback navegador).

Acessibilidade
- Sheet: foco inicial no campo URL; botões com labels claros; `contentDescription` nos ícones.
- Revisão: leitores anunciam campos e conteúdo; listas com ordem de foco previsível; ações de editar acessíveis por teclado.

Responsividade
- Mobile (MVP): 1 coluna. 
- ≥ 840 dp: manter 1 coluna neste MVP; sheet com largura máx. ~640 dp; cartões expandem na largura disponível.

Notas para Dev
- Sheet: `CookzyModalBottomSheet` com `rememberModalBottomSheetState(skipPartiallyExpanded = true)`.
- Validação de URL (YouTube): regex simples e normalização (youtu.be / youtube.com/watch). Disparar extração apenas se válido.
- Extração: exibir `LongOperationDialog` de tela cheia enquanto processa; ao sucesso, fechar loading e abrir `RecipeEditorScreen` (variante “Revisar importação”).
- Revisão: `RecipeEditorScreen` exibe “Link de origem” DENTRO do Card Cabeçalho com ação “Abrir link” (Intent ACTION_VIEW). Estado pré-preenchido pelos dados extraídos.
- Erros: em extração → sheet de erro; em salvar → Snackbar na tela de revisão.
- Duplicatas: opcional (futuro) — detectar por título+origem e oferecer “Manter ambas/Mesclar”.

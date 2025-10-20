# ⚙️ Seção 5 — Interações e Estados de Uso

## 🎯 Objetivo
Garantir que todas as ações dentro do Cookzy sejam **claras, previsíveis e responsivas**, criando uma experiência fluida do primeiro toque até a finalização do preparo da receita.  

---

## 🧭 1. Estados Globais

| Estado | Descrição | Feedback visual |
|--------|------------|-----------------|
| **Carregando** | Exibição de shimmer nos cards e placeholders em listas. | Gradiente animado Material Motion (1500ms). |
| **Vazio** | Quando não há livros, receitas ou resultados de busca. | Ilustração simpática + CTA contextual (“Adicionar Receita”, “Importar do YouTube”). |
| **Erro** | Falha em carregar, salvar ou importar dados. | Snackbar persistente com ícone ⚠️ e opção “Tentar novamente”. |
| **Offline** | Sem conexão durante uso. | Banner superior azul-acinzentado: “Modo offline — alterações serão sincronizadas”. |
| **Sucesso** | Ação concluída (salvar, excluir, importar). | Snackbar transitório + animação sutil de checkmark ✅. |

---

## 👆 2. Interações Principais

| Componente | Ação | Resultado esperado |
|-------------|------|--------------------|
| **FAB (Novo Livro / Receita)** | Toque curto → abre formulário; long-press → exibe dicas rápidas. | Expansão animada (scale + fade). |
| **RecipeCard / BookCard** | Toque → abre detalhe; swipe → ações rápidas (editar/excluir). | Ripple + leve elevação (4dp). |
| **MiniTimer** | Tap → expandir / colapsar; long-press → encerrar. | Transição scale + mudança de cor. |
| **Tooltip Ingrediente** | Tap no texto → abre tooltip; toque fora → fecha. | Fade-in/out (150ms) + scrim leve. |
| **Busca** | Digitação → resultados filtrados em tempo real. | Placeholder muda para “Buscando...”. |

---

## 🧩 3. Feedbacks Visuais Contextuais

### 📗 Editor de Receita
- **Salvando...** → loader circular no botão.  
- **Salvo com sucesso!** → Snackbar verde-claro + ícone ✅.  
- **Erro de validação** → Campo em vermelho + texto de ajuda “Campo obrigatório”.

### 🕒 Timers
- **Iniciado:** botão muda para “⏸ Pausar” com fundo laranja.  
- **Pausado:** botão volta para verde “▶ Retomar”.  
- **Finalizado:** Snackbar “Tempo concluído!” + vibração curta + som opcional.

### 🍴 Conversor de Medidas
- Mudança de unidade → highlight amarelo no campo atualizado.  
- Feedback auditivo opcional (click suave) ao alternar unidades.

---

## 🔁 4. Transições Entre Telas

| Origem → Destino | Animação | Justificativa |
|------------------|-----------|----------------|
| Home → Detalhe do Livro | Fade + slide horizontal | Mantém continuidade visual. |
| Livro → Receita | Shared element (imagem + título) | Cria sensação de contexto mantido. |
| Receita → Preparo | Fade-through | Foco total no conteúdo da receita. |
| Preparo → Autoavaliação | Slide-up | Enfatiza encerramento da jornada. |

---

## 🚨 5. Mensagens de Erro e Recuperação

| Cenário | Mensagem | Ação sugerida |
|----------|-----------|----------------|
| Falha ao importar vídeo | “Não foi possível ler os dados do vídeo.” | “Tentar novamente” / “Editar manualmente”. |
| Falha ao salvar receita | “Erro ao salvar. Verifique sua conexão.” | “Repetir” / “Salvar offline”. |
| API de nutrição indisponível | “Dados nutricionais temporariamente indisponíveis.” | Ocultar tabela até próxima sincronização. |

---

## 💬 6. Microinterações Sonoras e Táteis

- Vibração leve (30ms) ao iniciar timers e confirmar ações.  
- Feedback sonoro opcional ao concluir timers (notificação suave).  
- Nenhum som intrusivo em loops curtos ou notificações persistentes.  

---

## ♿ 7. Acessibilidade de Estados
- Todos os estados exibem **descrições textuais equivalentes** (não apenas ícones).  
- Snackbars e tooltips permanecem por tempo suficiente (mín. 5s).  
- Banners persistentes podem ser dispensados por teclado ou voz.  
- Vibrations respeitam configuração de acessibilidade do sistema (`haptic_feedback_enabled`).  

---

## 💡 8. Resumo de Padrões de Interação
- Tempo máximo de resposta: **100ms para feedbacks visuais**, **300ms para carregamentos curtos**.  
- Sempre fornecer **indicação de progresso** (botão, shimmer ou snackbar).  
- Evitar transições simultâneas (priorizar hierarquia de foco).  
- Animações curtas, suaves e reversíveis — sem loops contínuos.  

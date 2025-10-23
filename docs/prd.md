# 🧾 **PRD – Cookzy: Aplicativo de Receitas**

---

## 🎯 1. Visão Geral e Objetivos

**Propósito:**  
O Cookzy centraliza todas as receitas do usuário em um único aplicativo, permitindo **criar, organizar, buscar e preparar receitas** de forma prática, interativa e inteligente.  

**Objetivos principais:**
- Simplificar o gerenciamento e acesso às receitas.  
- Ajudar o usuário durante o preparo com timers, dicas visuais e vídeos.  
- Tornar a experiência de cozinhar mais envolvente e personalizada.  
- Permitir importação automática de receitas de plataformas populares.  

---

## 👥 2. Usuários-Alvo e Contexto

**Público-alvo:**  
- Pessoas que cozinham regularmente (domésticos, estudantes, iniciantes).  
- Usuários que colecionam receitas de fontes variadas (redes sociais, sites, vídeos).  
- Pessoas que desejam manter um registro nutricional e controle de porções.

**Cenários de uso:**  
- Consultar receitas já salvas e preparar passo a passo.  
- Criar ou editar receitas próprias.  
- Importar receitas de vídeos (YouTube, TikTok etc.) e segui-las com timers automáticos.  
- Calcular porções durante o preparo.  

---

## ⚙️ 3. Funcionalidades Principais (Epics)

### 🍳 Núcleo do Produto

#### CRUD de Receitas
Permitir criar, visualizar, editar e excluir receitas com persistência local.  
✅ Alterações persistem entre sessões.  

#### Livros de Receitas (Coleções)
Organizar receitas em coleções temáticas.  
✅ Criar, renomear, excluir livros; adicionar/remover receitas.

#### Busca e Filtros
Encontrar receitas rapidamente.  
✅ Busca por título, ingredientes e livros de receitas; resultados navegáveis.  

#### Importação de Receitas
Importar receitas de **YouTube** no MVP (expandir para outras fontes nas próximas versões).  
✅ Fluxo: Sheet “Importar receita (origem/URL)” → tela de loading em tela cheia → revisão na tela do **Editor (variante Revisar importação)** com **link de origem** no cabeçalho → salvar → Detalhe da Receita.  
✅ Extração automática de dados (título, ingredientes, preparo) com possibilidade de edição antes de salvar.  

---

### ⏱️ Assistência no Preparo

#### Timer de Cozimento Automático
Detecta menções de tempo nos passos (“cozinhe por 10min”) e permite iniciar, pausar e redefinir o timer.  
✅ Notificação ao término.  

#### Tempo Total de Preparo
Registra tempo total entre início e fim do preparo.  

#### Emojis e Pistas Visuais
Representa ações com ícones (⌛, 🔥) para facilitar leitura.  

#### Menções de Ingredientes Interativas
Ao tocar em um ingrediente citado, exibe tooltip com o texto original do ingrediente.  

#### Vídeo com Pontos Ancorados
Sem player interno no MVP. Exibir **CTA “Abrir vídeo externo (timestamp)”** quando houver origem (YouTube/Instagram) e abrir via Intent.  
✅ Normalização de timestamp; fallback para abrir no navegador ou copiar link se app externo indisponível.  

#### Barra de Preparo (mini‑timer persistente)
Exibe o timer ativo de forma persistente no rodapé do app, com título, tempo restante, play/pause e avançar.
✅ Não renderiza quando a tela de Preparo estiver aberta; toque abre a tela de Preparo.  

---

### 🧮 Cálculos e Nutrição

#### Escalonamento de Porções
Ajusta quantidades dos ingredientes conforme o número de porções desejadas.  
✅ Atualiza automaticamente as quantidades e a tabela nutricional.  

#### Tabela Nutricional
Mostra calorias, macronutrientes e micronutrientes principais.  
✅ Dados ajustáveis por porção.  

---

### 🌍 Comunidade e Interação

#### Compartilhamento
Gerar link para compartilhar receitas.  

#### Autoavaliação
Permitir que o usuário avalie e comente após o preparo.  
✅ Avaliação associada ao usuário.  

---

## 🧭 4. Mapeamento de Fluxos e Telas

**A. Descoberta & Navegação**  
1. Home — carrossel de receitas recentes, carrossel de livros (filtro), lista/grade de receitas, **App Bar com busca inline** e **FAB → Sheet “Adicionar receita”**  
2. Resultados de busca  

**B. Receita**  
3. Detalhe da receita (abas: Ingredientes / Preparo / Nutrição) com **Stepper de porções inline (1–99)**  
4. Editor de receita (criar/editar)  
5. Preparo passo a passo  
6. Tabela nutricional  
7. **CTA “Abrir vídeo externo (timestamp)”**  

**C. Coleções**  
8. Lista de livros  
9. Detalhe do livro  

**D. Importação**  
10. Sheet: origem/URL (YouTube no MVP)  
11. Loading (tela cheia)  
12. Revisar importação no Editor (com link de origem)  

**E. Utilitários**  
13. Compartilhar (sheet)  

**F. Pós-Uso**  
14. Autoavaliação  

**Componentes transversais:**  
- **Barra de Preparo (mini‑timer persistente)**  
- Tooltip de ingrediente  
- Bottom sheets (ex.: Compartilhar, Importar) com header consistente e footer vertical full‑width  
- Notificações locais  

**Fluxos principais:**  
1. Criar receita: Home → FAB → (Importar/Manual) → Editor → Salvar → Detalhe  
2. Buscar: Home → Busca → Resultados → Detalhe  
3. Cozinhar: Detalhe → Preparo (PrepBar ativa fora da tela de Preparo); CTA de vídeo abre app externo → Autoavaliação  
4. Importar: Sheet Origem/URL → Loading (tela cheia) → Revisão no Editor (com link de origem) → Salvar → Detalhe  
5. Livros: Lista → Detalhe → Receita  

---

## 📈 5. Métricas de Sucesso

- ≥ 80% de retenção de usuários após 30 dias  
- ≥ 70% das receitas criadas com porções ajustadas  
- ≥ 60% das receitas contendo avaliação  
- ≥ 50% dos usuários usando importação de plataformas externas  

---

## ⚠️ 6. Riscos e Restrições Técnicas

- **Extração de dados externos:** depende de parsing de conteúdo HTML que pode mudar.  
- **Vídeos externos:** sem player interno no MVP; abertura via Intent ACTION_VIEW com fallback para navegador; possíveis restrições de acesso (privacidade, API).  
- **Nutrição:** depende de base de dados confiável e atualizada.  
- **Offline:** sincronização de dados deve ser projetada cuidadosamente.  

---

## 🧩 7. Integrações e Dependências

- Integração com APIs públicas das plataformas (YouTube, TikTok, etc.).  
- Serviços de persistência local (Room / SQLite).  
- Possível uso de API nutricional externa.  
- Sistema de notificações locais e agendamento de timers.  
- Intents para apps de vídeo (YouTube/Instagram) com normalização de timestamps.  

---

## ✅ 8. Critérios de Aceitação Globais / Escopo Inicial

- CRUD de receitas totalmente funcional e persistente.  
- Timer automático por passo.  
- Importação funcional de ao menos **YouTube**, com loading em tela cheia e revisão no Editor incluindo link de origem.  
- Escalonamento de porções e tabela nutricional operantes.  
- Experiência fluida entre etapas de preparo, vídeo e avaliação.
- Stepper de porções inline (faixa 1–99, persistência por receita) recalculando ingredientes e nutrição.
- Barra de Preparo ativa fora da tela de Preparo e oculta quando a tela de Preparo estiver visível.
- Vídeo externo aberto via CTA com timestamp; fallback para navegador/cópia de link.
- Bottom sheets padronizadas (header consistente; footer vertical full‑width quando aplicável).

---

## 📐 9. Requisitos Não Funcionais (NFRs)

- Acessibilidade: conformidade com WCAG 2.2 AA e diretrizes Android; alvos ≥ 48dp; labels/semantics adequadas; suporte a `fontScale` até 200%.
- Responsividade: manter layout de 1 coluna no MVP; ajustes progressivos para ≥ 840 dp sem Navigation Rail.
- Desempenho: interações < 100 ms; animações estáveis (≈ 60 fps) nos fluxos principais; uso de skeletons/placeholders para evitar layout shift.
- Consistência Visual: tokens de tema conforme `Theme.kt`, `Color.kt`, `Type.kt` (Material 3), incluindo `ExtendedColorScheme.success`.

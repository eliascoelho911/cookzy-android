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
- Calcular porções e converter medidas durante o preparo.  

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
Importar receitas de **YouTube, TikTok, Instagram e Facebook**.  
✅ Extração automática de dados (título, ingredientes, preparo); edição antes de salvar.  

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
Relaciona passos do preparo com timestamps no vídeo original.  

---

### 🧮 Cálculos e Nutrição

#### Conversor de Medidas
Conversão entre xícaras, ml e colheres.  
✅ Recalcula valores dinamicamente.  

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
1. Home — lista de livros e receitas  
2. Resultados de busca  

**B. Receita**  
3. Detalhe da receita (abas: Ingredientes / Preparo / Nutrição)  
4. Escalonar porções  
5. Editor de receita (criar/editar)  
6. Preparo passo a passo  
8. Tabela nutricional  
9. Player de vídeo (timestamps)  

**C. Coleções**  
10. Lista de livros  
11. Detalhe do livro  

**D. Importação**  
12. Escolher origem (YouTube, TikTok, etc.)  
13. Revisar e confirmar dados extraídos  

**E. Utilitários**  
14. Conversor de medidas  
15. Compartilhar receita  

**F. Pós-Uso**  
16. Autoavaliação  

**Componentes transversais:**  
- Mini-timer flutuante  
- Tooltip de ingrediente  
- Bottom sheets (Conversor, Compartilhar)  
- Notificações locais  

**Fluxos principais:**  
1. Criar receita: Home → Editor → Salvar → Detalhe  
2. Buscar: Home → Busca → Resultados → Detalhe  
3. Cozinhar: Detalhe → Preparo ↔ Vídeo → Autoavaliação  
4. Importar: Origem → Revisão → Salvar → Detalhe  
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
- **Vídeos externos:** podem ter restrições de acesso (privacidade, API).  
- **Nutrição:** depende de base de dados confiável e atualizada.  
- **Offline:** sincronização de dados deve ser projetada cuidadosamente.  

---

## 🧩 7. Integrações e Dependências

- Integração com APIs públicas das plataformas (YouTube, TikTok, etc.).  
- Serviços de persistência local (Room / SQLite).  
- Possível uso de API nutricional externa.  
- Sistema de notificações locais e agendamento de timers.  

---

## ✅ 8. Critérios de Aceitação Globais / Escopo Inicial

- CRUD de receitas totalmente funcional e persistente.  
- Timer automático por passo.  
- Importação funcional de ao menos **YouTube**.  
- Escalonamento de porções e tabela nutricional operantes.  
- Experiência fluida entre etapas de preparo, vídeo e avaliação.

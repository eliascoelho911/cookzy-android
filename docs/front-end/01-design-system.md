# 🎨 Seção 4 — Design System, Cores e Tipografia

## 🌈 1. Identidade Visual

O design do Cookzy comunica **acolhimento, leveza e organização**, inspirado em utensílios de cozinha e texturas naturais.  
As cores transmitem frescor e clareza — sem distrair do conteúdo principal: as receitas.

---

## 🎨 2. Paleta de Cores

| Categoria | Light Mode | Dark Mode | Uso principal |
|------------|-------------|------------|----------------|
| **Primary** | `#4CAF50` | `#81C784` | Ações primárias (salvar, confirmar) |
| **OnPrimary** | `#FFFFFF` | `#003300` | Texto sobre fundo primário |
| **Secondary** | `#FFC107` | `#FFD54F` | Ícones e destaques visuais |
| **OnSecondary** | `#000000` | `#1E1E1E` | Texto sobre fundo secundário |
| **Tertiary** | `#FF7043` | `#FF8A65` | Feedbacks visuais, timers |
| **Surface** | `#FFFFFF` | `#121212` | Cards e planos principais |
| **SurfaceVariant** | `#F2F2F2` | `#1E1E1E` | Containers, listas, tooltips |
| **Outline** | `#BDBDBD` | `#3A3A3A` | Bordas sutis e divisores |
| **Error** | `#E53935` | `#FF8A80` | Falhas, exclusão de item |
| **Success** | `#66BB6A` | `#A5D6A7` | Confirmações |
| **Scrim** | `#00000066` | `#00000099` | Fundo de tooltips, modais |

As cores seguem o padrão **Material 3 Dynamic Color**, garantindo contraste mínimo de 4.5:1.

---

## ✍️ 3. Tipografia

| Estilo | Fonte | Tamanho | Peso | Uso |
|--------|--------|---------|------|-----|
| **DisplayLarge** | Nunito | 32sp | Bold | Títulos principais (Home, Livro) |
| **HeadlineMedium** | Nunito | 24sp | SemiBold | Seções internas (Preparo, Nutrição) |
| **TitleMedium** | Nunito | 20sp | Medium | Abas e cabeçalhos de cards |
| **BodyLarge** | Open Sans | 16sp | Regular | Texto de receita |
| **BodyMedium** | Open Sans | 14sp | Regular | Instruções e tooltips |
| **LabelSmall** | Open Sans | 12sp | Medium | Badges, contadores e botões pequenos |

Ajustes automáticos:  
- *DynamicType* habilitado (Android).  
- *Line height* +10% para melhor legibilidade durante leitura de preparo.

---values

## 🧱 4. Componentes e Tokens de Estilo

| Token | Valor | Aplicação |
|--------|--------|-----------|
| `cornerRadius.small` | 8dp | Cards e imagens pequenas |
| `cornerRadius.medium` | 16dp | Containers principais |
| `elevation.level1` | 2dp | Cards leves |
| `elevation.level2` | 4dp | FABs, tooltips |
| `padding.standard` | 16dp | Layouts de tela |
| `padding.compact` | 8dp | Cards e botões pequenos |
| `spacing.listItem` | 12dp | Itens de lista |
| `icon.size.standard` | 24dp | Ícones padrão |
| `icon.size.large` | 32dp | Ícones de destaque (timer, play) |

---

## 🌘 5. Modo Escuro (Tokens Específicos)

- **Neutral Containers:**  
  - `neutralContainer = #1E1E1E`  
  - `neutralContainerVariant = #2A2A2A`  
- **Elevation Overlay:** aplicada apenas a `surface` e `surfaceVariant`.  
- **Cards:** borda sutil `#3A3A3A` + sombra mínima.  
- **Texto:** `#FFFFFF` (primário), `#FFFFFFB3` (secundário), `#FFFFFF80` (desabilitado).

---

## ✨ 6. Microinterações e Motion

| Interação | Tipo | Duração | Easing | Contexto |
|------------|------|----------|---------|----------|
| Card tap | Scale-in + ripple | 120ms | LinearOutSlowIn | Navegação |
| Timer start | Scale + color shift | 250ms | Standard | Início do timer |
| Troca de aba | Fade-through | 200ms | Standard | Navegação de aba |
| Recalcular nutrição | Fade + highlight verde | 180ms | EaseInOut | Atualização dinâmica |

---

## ♿ 7. Acessibilidade Integrada

- Contraste mínimo 4.5:1 garantido para textos.  
- Compatível com **TalkBack** e **Dynamic Font Size**.  
- Gestos alternativos para *tooltip*, *timer* e *FAB*.  
- Testado em temas escuro/claro e fontes ≥130%.

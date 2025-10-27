# Epic 01 — CRUD de Receitas

## Objetivo
Permitir criar, visualizar, editar e excluir receitas com persistência local e experiência fluida.

## Contexto
Epic central do produto. Sustenta todos os demais fluxos (busca, preparo, nutrição, compartilhamento).

## Escopo
- Em escopo: criar/editar/excluir/visualizar receitas; persistência local; estados de carregamento/erro/vazio; abas Ingredientes/Preparo/Nutrição.
- Fora de escopo (MVP): sincronização em nuvem; compartilhamento externo; colaboração multiusuário.
- Itens tratados em outros épicos (fora do Épico 01): Stepper de porções, Resumo Nutritivo, Prep Bar (mini‑timer persistente) e CTA de vídeo externo (timestamp).

## Requisitos Funcionais
- Criar receita com campos: título, ingredientes em texto livre (um por linha, `rawText`) e preparo em campo único; livros de receitas e mídia opcionais.
- Inferir quantidade dos ingredientes a partir de `rawText` quando possível, sem persistir (recalculada on-demand). A unidade do ingrediente não é armazenada.
- Editar e excluir receitas com confirmação de exclusão.
- Persistir dados localmente (Room/SQLite) e manter entre sessões.
- Exibir detalhe com navegação por abas e estados padrão (erro, vazio, offline).

## Critérios de Aceitação (alto nível)
- [ ] Operações CRUD funcionam e persistem após encerrar/reabrir o app.
- [ ] Validações básicas de campos com feedback ao usuário.
- [ ] Navegação consistente: Home → Detalhe → Editor → Salvar/Cancelar.

## Dependências
- Camada de persistência local (Room/SQLite).
- Arquitetura de ViewModels e contratos de estado/evento/efeito (docs/ui-architecture.md).

## Riscos
- Perda de dados por esquemas mal versionados; mitigar com migrações testadas.

## Métricas de Sucesso (ligação PRD)
- Contribui para retenção ≥ 80% em 30 dias via base funcional sólida.

## Stories sugeridas
1. Modelo e DAO de Receita (Room) + migração inicial.
2. Tela Editor de Receita (criar/editar) com validações.
3. Detalhe da Receita (abas) com estados de erro/vazio.
4. Exclusão com confirmação e undo (snackbar).
 5. Lista de receitas (Home): carrossel de recentes (1 por viewport), alternância lista/grade, busca inline; sem carrossel de livros no Épico 01.

## Planejamento — Próxima Sprint

Priorizar as histórias de redesign alinhadas aos protótipos Compose, na ordem:

1) 1.4 — Redesenhar Home/Lista de Receitas (recentes 1/viewport, lista↔grade, busca inline; sem carrossel de livros)
2) 1.5 — Redesenhar Editor de Receitas (layout em cards; reordenação; validações; sem Porções/Nutrição/Livros/Link de origem)
3) 1.6 — Redesenhar Detalhe da Receita (abas estáveis; rawText com quantidade derivada; sem Stepper/Resumo/PrepBar/CTA vídeo)

Notas:
- Itens fora do escopo do Épico 01 permanecem adiados para épicos específicos (Cálculos/Nutrição, Preparo, Importação, Coleções).
- Confirmar refinamento técnico antes de iniciar (PO/SM), mantendo as histórias fora de “Ready for Dev” até conclusão do refinamento.

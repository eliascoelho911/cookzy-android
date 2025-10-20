# Epic 01 — CRUD de Receitas

## Objetivo
Permitir criar, visualizar, editar e excluir receitas com persistência local e experiência fluida.

## Contexto
Epic central do produto. Sustenta todos os demais fluxos (busca, preparo, nutrição, compartilhamento).

## Escopo
- Em escopo: criar/editar/excluir/visualizar receitas; persistência local; estados de carregamento/erro/vazio; abas Ingredientes/Preparo/Nutrição.
- Fora de escopo (MVP): sincronização em nuvem; compartilhamento externo; colaboração multiusuário.

## Requisitos Funcionais
- Criar receita com campos: título, ingredientes (nome, quantidade, unidade, observações), passos, tags, mídia opcional.
- Editar e excluir receitas com confirmação de exclusão.
- Persistir dados localmente (Room/SQLite) e manter entre sessões.
- Exibir detalhe com navegação por abas e estados padrão (erro, vazio, offline).

## Critérios de Aceitação (alto nível)
- [ ] Operações CRUD funcionam e persistem após encerrar/reabrir o app.
- [ ] Validações básicas de campos com feedback ao usuário.
- [ ] Navegação consistente: Home → Detalhe → Editor → Salvar/Cancelar.

## Dependências
- Camada de persistência local (Room/SQLite).
- Arquitetura de ViewModels e contratos de estado/evento (docs/front-end/03-arquitetura-componentes.md).

## Riscos
- Perda de dados por esquemas mal versionados; mitigar com migrações testadas.

## Métricas de Sucesso (ligação PRD)
- Contribui para retenção ≥ 80% em 30 dias via base funcional sólida.

## Stories sugeridas
1. Modelo e DAO de Receita (Room) + migração inicial.
2. Tela Editor de Receita (criar/editar) com validações.
3. Detalhe da Receita (abas) com estados de erro/vazio.
4. Exclusão com confirmação e undo (snackbar).


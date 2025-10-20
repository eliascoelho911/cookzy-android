#+ Epic 04 — Importação de Receitas

## Objetivo
Importar receitas de YouTube (MVP) com extração automática de dados e revisão antes de salvar.

## Contexto
Canal de aquisição e praticidade: reduz fricção para trazer conteúdo externo para o Cookzy.

## Escopo
- Em escopo: origem YouTube; extrair título/ingredientes/preparo; tela de revisão e edição; salvar como receita.
- Fora de escopo (MVP): TikTok/Instagram/Facebook; download de vídeo; parsing avançado de HTML fora do básico.

## Requisitos Funcionais
- Selecionar origem (YouTube) e colar URL.
- Extrair dados mínimos e exibir tela de revisão.
- Permitir correções antes de salvar receita final.

## Critérios de Aceitação (alto nível)
- [ ] URLs válidas de YouTube geram rascunho de receita editável.
- [ ] Usuário consegue revisar/editar e salvar.
- [ ] Erros de parsing mostram feedback e opção de editar manualmente.

## Dependências
- Rede e parsing; persistência do Epic 01.

## Riscos
- Mudanças no HTML da plataforma; mitigar com fallback de edição manual.

## Métricas de Sucesso
- ≥ 50% dos usuários usam importação externa (meta PRD).

## Stories sugeridas
1. UI Origem/Entrada de URL (YouTube) + validação.
2. Módulo de extração básica e mapeamento para modelo local.
3. Tela de Revisão/Confirmação com salvar em Receita.


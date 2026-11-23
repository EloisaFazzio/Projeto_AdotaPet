# AdotaPet — Marco da entrega acadêmica v1.0.0-academica

**Situação:** versão acadêmica integrada, com demonstração visual pública gratuita. A tag e a [Release acadêmica v1.0.0-academica](https://github.com/EloisaFazzio/Projeto_AdotaPet/releases/tag/v1.0.0-academica) foram criadas no GitHub após testes Java e verificação de sintaxe JavaScript aprovados. Trata-se de um marco acadêmico, **não** de uma versão de produção.

## Links oficiais

- [Código-fonte completo (main)](https://github.com/EloisaFazzio/Projeto_AdotaPet)
- [Demonstração pública do repositório — GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)
- Endereço anterior do Cloudflare Pages: retirado dos links de apresentação após abrir conteúdo diferente do AdotaPet.
- [Guia de execução local](HOMOLOGACAO_LOCAL.md)
- [Auditoria de arquivos e limpeza](AUDITORIA_FINAL.md)
- [Pull Request de integração #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20)
- [Pull Request de limpeza #23](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/23)

## Conteúdo entregue

1. Backend Java 17/Spring Boot 3.3.5 com API REST, JPA, H2 para desenvolvimento e PostgreSQL configurável.
2. Frontend original responsivo, com páginas do catálogo e painel, arquivos CSS/JS e fotografias estáticas.
3. Integração na branch principal e documentação técnica em `docs/`.
4. Testes automatizados de backend, frontend e homologação full stack; as três verificações da PR #23 concluíram com sucesso.
5. Site demonstrativo gratuito publicado pelo GitHub Pages em **https://eloisafazzio.github.io/Projeto_AdotaPet/**, com o frontend do projeto e registros **fictícios**. O domínio antigo do Cloudflare Pages está sob revisão e não é recomendado para a apresentação.
6. README atualizado e auditoria de recursos não referenciados; 18 arquivos estáticos redundantes removidos com histórico preservado no Git.

## O que a demonstração NÃO faz

O GitHub Pages, nesta configuração, serve arquivos estáticos. O endereço público **não executa** Spring Boot nem PostgreSQL. Não permite login, cadastro real, solicitações reais de adoção ou administração. Não apresentar o site como plataforma real de adoções em operação.

## Pendências honestamente preservadas

- Revisão de segurança, privacidade, migrações e testes para produção: [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13).
- Campos de imagem e rotas de upload/consulta já existem em código, porém o tratamento definitivo e a validação das mídias para uso real permanecem na [Issue #18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18).
- A [Issue #19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) foi encerrada para o escopo acadêmico após confirmação funcional da responsável, sem exigência de anexos ou prints. Esse encerramento não equivale a homologação de produção.
- A [triagem de Issues](TRIAGEM_ISSUES_ENTREGA.md) identifica critérios implementados, pendentes ou não completamente homologados, sem encerrar tarefas não comprovadas.

## Política de custos

A equipe optou por **não gerar cobranças**. O domínio `adotapet.com.br` não foi contratado; o Railway não hospeda o backend, e o **GitHub Pages** é o endereço divulgado da demonstração gratuita. O endereço do Cloudflare Pages necessita correção/verificação independente.

## Revisão final para apresentação

- Link da demonstração vinculada ao repositório: **https://eloisafazzio.github.io/Projeto_AdotaPet/** (workflow de publicação conferido; confirmar visualmente no navegador antes da apresentação).
- Código-fonte completo, documentação e testes automatizados mantidos no GitHub.
- Pendências reais de segurança, privacidade e preparação para produção continuam registradas nas Issues futuras #7, #13 e #18, sem divulgação indevida como produção.
- Revisão visual do site em desktop e celular: **confirmada pela responsável**; símbolos e setas corrigidos e aprovados em 22/11/2026. Testes automatizados não substituem revisão de comportamento do backend.
- Não compartilhar credenciais, banco de dados real ou links locais `localhost` como se fossem públicos.

## Revisão de consistência do domínio — 22/11/2026

- O diagrama apresenta **seis agrupamentos conceituais**, enquanto o código preserva **oito entidades JPA**. A diferença é intencional, não uma entidade omitida.
- O Dicionário de Dados e o Mapeamento JPA foram alinhados aos campos opcionais `imagem_dados` e `imagem_mime` das entidades `Animal` e `Vacina`.
- Os endpoints de rastreabilidade especificados estão presentes nos controladores, e testes de API constam de `RastreabilidadeApiTest`; permanecem evoluções de testes/performance antes da produção.
- As 13 Issues originalmente abertas foram triadas no [registro próprio](TRIAGEM_ISSUES_ENTREGA.md). A #16 foi encerrada após conferência; após confirmação de funcionamento pela responsável, outras **9 foram encerradas para o escopo acadêmico** (#2, #3, #4, #5, #6, #8, #11, #12 e #19). **Somente 3 seguem abertas** (#7, #13 e #18), classificadas como evolução futura. O encerramento acadêmico não comprova uma auditoria de segurança independente nem prontidão para produção.
- A responsável **dispensou prints e anexos de evidências** nesta etapa; não afirmar que houve testes manuais além dos que ela confirmou.

## Identificação de versão

**Identificador documental:** `v1.0.0-academica`.

**Tag Git e Release publicadas:** [v1.0.0-academica](https://github.com/EloisaFazzio/Projeto_AdotaPet/releases/tag/v1.0.0-academica), associadas ao commit `e6ee3ed6d1aad5dee3b9fa8e8211b56590962978`. Os testes Java e a sintaxe JavaScript foram aprovados no [workflow da Release](https://github.com/EloisaFazzio/Projeto_AdotaPet/actions/runs/44982576842). Esta é uma **pré-release acadêmica**, não uma declaração de prontidão do backend para produção.

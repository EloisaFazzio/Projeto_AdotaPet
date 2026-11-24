# AdotaPet — Marco da entrega acadêmica v1.0.0-academica

**Situação:** organizei a versão acadêmica integrada, com demonstração pública gratuita. Publiquei a [Release v1.0.0-academica](https://github.com/EloisaFazzio/Projeto_AdotaPet/releases/tag/v1.0.0-academica) no GitHub após os testes Java e de sintaxe JavaScript aprovados.

## Links oficiais

- [Código-fonte completo (main)](https://github.com/EloisaFazzio/Projeto_AdotaPet)
- [Demonstração pública do repositório — GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)
- [Guia de execução local](HOMOLOGACAO_LOCAL.md)
- [Auditoria de arquivos e limpeza](AUDITORIA_FINAL.md)
- [Pull Request de integração #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20)
- [Pull Request de limpeza #23](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/23)

## Conteúdo entregue

1. Backend Java 17/Spring Boot 3.3.5 com API REST, JPA, H2 para desenvolvimento e PostgreSQL configurável.
2. Frontend original responsivo, com páginas do catálogo e painel, arquivos CSS/JS e fotografias estáticas.
3. Integração na branch principal e documentação técnica em `docs/`.
4. Testes automatizados de backend, frontend e homologação full stack; as três verificações da PR #23 concluíram com sucesso.
5. Publiquei o site demonstrativo gratuito pelo GitHub Pages em **https://eloisafazzio.github.io/Projeto_AdotaPet/**, com o frontend e registros fictícios.
6. README atualizado e auditoria de recursos não referenciados; 18 arquivos estáticos redundantes removidos com histórico preservado no Git.

## O que a demonstração NÃO faz

O GitHub Pages, nesta configuração, serve arquivos estáticos. O endereço público **não executa** Spring Boot nem PostgreSQL. Não permite login, cadastro real, solicitações reais de adoção ou administração. Não apresentar o site como plataforma real de adoções em operação.

## Pendências honestamente preservadas

- Revisão de segurança, privacidade, migrações e testes para produção: [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13).
- Campos de imagem e rotas de upload/consulta já existem em código, porém o tratamento definitivo e a validação das mídias para uso real permanecem na [Issue #18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18).
- Encerrei a [Issue #19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) após confirmar o funcionamento do sistema local para a entrega acadêmica.
- A [triagem de Issues](TRIAGEM_ISSUES_ENTREGA.md) identifica critérios implementados, pendentes ou não completamente homologados, sem encerrar tarefas não comprovadas.

## Política de custos

Optei pela publicação gratuita da demonstração no **GitHub Pages**. O backend completo permanece disponível para execução local.

## Revisão final para apresentação

- Link da demonstração vinculada ao repositório: **https://eloisafazzio.github.io/Projeto_AdotaPet/** (workflow de publicação conferido; confirmar visualmente no navegador antes da apresentação).
- Código-fonte completo, documentação e testes automatizados mantidos no GitHub.
- Pendências reais de segurança, privacidade e preparação para produção continuam registradas nas Issues futuras #7, #13 e #18, sem divulgação indevida como produção.
- Conferi o visual do site no computador e no celular e aprovei as correções dos símbolos e das setas.
- Não compartilhar credenciais, banco de dados real ou links locais `localhost` como se fossem públicos.

## Revisão de consistência do domínio — 22/11/2026

- O diagrama apresenta **seis agrupamentos conceituais**, enquanto o código preserva **oito entidades JPA**. A diferença é intencional, não uma entidade omitida.
- O Dicionário de Dados e o Mapeamento JPA foram alinhados aos campos opcionais `imagem_dados` e `imagem_mime` das entidades `Animal` e `Vacina`.
- Os endpoints de rastreabilidade especificados estão presentes nos controladores, e testes de API constam de `RastreabilidadeApiTest`; permanecem evoluções de testes/performance antes da produção.
- Revisei as Issues no [registro de triagem](TRIAGEM_ISSUES_ENTREGA.md). Encerrei a #16 e outras **9 Issues** relacionadas à entrega acadêmica (#2, #3, #4, #5, #6, #8, #11, #12 e #19). Mantive **3 abertas** (#7, #13 e #18) para evolução futura.

## Identificação de versão

**Identificador documental:** `v1.0.0-academica`.

**Tag Git e Release publicadas:** [v1.0.0-academica](https://github.com/EloisaFazzio/Projeto_AdotaPet/releases/tag/v1.0.0-academica), associadas ao commit `e6ee3ed6d1aad5dee3b9fa8e8211b56590962978`. Os testes Java e a sintaxe JavaScript foram aprovados no [workflow da Release](https://github.com/EloisaFazzio/Projeto_AdotaPet/actions/runs/44982576842). Esta é a versão identificada para a minha entrega acadêmica.

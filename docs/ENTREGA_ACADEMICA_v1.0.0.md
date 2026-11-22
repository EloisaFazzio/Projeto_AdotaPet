# AdotaPet — Marco da entrega acadêmica v1.0.0-academica

**Situação:** versão acadêmica integrada, com demonstração visual pública gratuita. Este documento identifica um **marco de entrega**, não uma versão de produção nem uma tag Git já criada.

## Links oficiais

- [Código-fonte completo (main)](https://github.com/EloisaFazzio/Projeto_AdotaPet)
- [Demonstração pública oficial — Cloudflare Pages](https://adotapet.pages.dev/)
- [Espelho gratuito — GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)
- [Guia de execução local](HOMOLOGACAO_LOCAL.md)
- [Auditoria de arquivos e limpeza](AUDITORIA_FINAL.md)
- [Pull Request de integração #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20)
- [Pull Request de limpeza #23](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/23)

## Conteúdo entregue

1. Backend Java 17/Spring Boot 3.3.5 com API REST, JPA, H2 para desenvolvimento e PostgreSQL configurável.
2. Frontend original responsivo, com páginas do catálogo e painel, arquivos CSS/JS e fotografias estáticas.
3. Integração na branch principal e documentação técnica em `docs/`.
4. Testes automatizados de backend, frontend e homologação full stack; as três verificações da PR #23 concluíram com sucesso.
5. Site demonstrativo gratuito disponibilizado no Cloudflare Pages em **https://adotapet.pages.dev/**, com o frontend do projeto e registros **fictícios**. O GitHub Pages permanece como espelho alternativo.
6. README atualizado e auditoria de recursos não referenciados; 18 arquivos estáticos redundantes removidos com histórico preservado no Git.

## O que a demonstração NÃO faz

Cloudflare Pages e GitHub Pages, nesta configuração, servem arquivos estáticos. O endereço público **não executa** Spring Boot nem PostgreSQL. Não permite login, cadastro real, solicitações reais de adoção ou administração. Não apresentar o site como plataforma real de adoções em operação.

## Pendências honestamente preservadas

- Revisão de segurança, privacidade, migrações e testes para produção: [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13).
- Foto individual, upload e tratamento definitivo de mídias de animais: [Issue #18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18).
- Evidências manuais complementares da integração: [Issue #19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19).
- Demais Issues de requisitos devem ser analisadas pelos responsáveis antes de encerrar, pois alguns critérios de aceite são mais amplos que os testes automatizados.

## Política de custos

A equipe optou por **não gerar cobranças**. O domínio `adotapet.com.br` não foi contratado; o Railway não hospeda o backend, e o **Cloudflare Pages** é o endereço público principal da demonstração gratuita; o GitHub Pages permanece como espelho.

## Revisão final para apresentação

- Link público principal: **https://adotapet.pages.dev/** (informado como funcionando pela responsável; conferir acessibilidade e recursos visuais no navegador antes da apresentação).
- Código-fonte completo, documentação e testes automatizados mantidos no GitHub.
- Pendências reais de segurança, privacidade e validação do backend continuam registradas em Issues abertas, sem divulgação indevida como produção.
- Conferir no navegador desktop e celular se fotos, fontes, filtros, navegação e avisos de demonstração são apresentados corretamente.
- Não compartilhar credenciais, banco de dados real ou links locais `localhost` como se fossem públicos.

## Identificação de versão

**Identificador documental:** `v1.0.0-academica`.

A tag Git formal e uma Release do GitHub não são criadas automaticamente por este documento. Se necessárias para entrega, o responsável pelo repositório deve criar a tag no commit final aprovado. Não confundir o documento com um registro de tag.

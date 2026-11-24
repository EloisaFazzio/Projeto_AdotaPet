# Triagem das Issues — AdotaPet (entrega acadêmica, 2026)

## Objetivo

Revisamos as Issues do AdotaPet para organizar as funcionalidades concluídas na entrega acadêmica e as melhorias que pretendemos considerar em versões futuras. Comparamos a implementação disponível no repositório com a documentação, os testes e o funcionamento do projeto.

## Resultado da triagem

Após a conferência e os testes do AdotaPet, encerramos **10 Issues** relacionadas ao escopo da entrega acadêmica: #2, #3, #4, #5, #6, #8, #11, #12, #16 e #19. Mantivemos **3 Issues abertas** (#7, #13 e #18) como propostas de evolução futura.

| Issue | Funcionalidade | Situação |
| --- | --- | --- |
| [#2](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/2) | Catálogo de animais e filtros | Concluída para a entrega acadêmica |
| [#3](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/3) | Cadastro, login e permissões | Concluída para a entrega acadêmica |
| [#4](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/4) | Solicitações de adoção | Concluída para a entrega acadêmica |
| [#5](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/5) | Abrigos, animais e capacidade | Concluída para a entrega acadêmica |
| [#6](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/6) | Vacinação, adoção e acompanhamento | Concluída para a entrega acadêmica |
| [#7](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/7) | Qualidade, relatórios e LGPD | Evolução futura |
| [#8](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/8) | Identidade visual e acessibilidade | Concluída para a entrega acadêmica |
| [#11](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/11) | Gestão administrativa de usuários e abrigos | Concluída para a entrega acadêmica |
| [#12](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/12) | Painel da ONG | Concluída para a entrega acadêmica |
| [#13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13) | Segurança, privacidade e implantação | Evolução futura |
| [#16](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/16) | Endpoints e Swagger | Concluída para a entrega acadêmica |
| [#18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18) | Tratamento definitivo de fotografias | Evolução futura |
| [#19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) | Integração do sistema | Concluída para a entrega acadêmica |

## Conferência entre código e documentação

- Organizamos o [Diagrama de Classe](Diagrama%20de%20Classe.svg) em **seis grupos conceituais**, mantendo as **oito entidades JPA** implementadas: `Usuario`, `Abrigo`, `Animal`, `Vacina`, `VacinacaoAnimal`, `SolicitacaoAdocao`, `Adocao` e `VisitaAcompanhamento`.
- Atualizamos o [Dicionário de Dados](Dicion%C3%A1rio%20de%20Dados.md) e o [Mapeamento de Entidades JPA](Mapeamento%20de%20Entidades%20JPA.md) para incluir os campos de imagem de `Animal` e `Vacina`.
- Conferimos as rotas da API e os testes de rastreabilidade documentados em [Rastreabilidade e Swagger](API_RASTREABILIDADE_E_SWAGGER.md).
- Verificamos as execuções de testes automatizados no GitHub Actions e confirmamos o funcionamento visual do projeto no computador e no celular.

## Entrega acadêmica

Disponibilizamos o [site demonstrativo](https://eloisafazzio.github.io/Projeto_AdotaPet/), o [código-fonte](../README.md), o [documento da entrega acadêmica](ENTREGA_ACADEMICA_v1.0.0.md) e o [guia de execução local](HOMOLOGACAO_LOCAL.md).

Mantemos as Issues #7, #13 e #18 registradas para futuras melhorias. A demonstração online apresenta o frontend com dados fictícios; o sistema completo, com API Java e banco de dados, pode ser executado localmente.

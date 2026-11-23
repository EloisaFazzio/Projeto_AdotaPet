# Triagem das Issues — AdotaPet (entrega acadêmica, 22/11/2026)

**Objetivo:** diferenciar os requisitos já presentes no código das pendências para uso real. Esta triagem não substitui a conferência detalhada de todos os critérios de aceite, e **não exige prints ou anexos de evidência**, por orientação da responsável.

**Escopo do produto público:** demonstração estática com dados fictícios, disponibilizada pelo GitHub Pages em https://eloisafazzio.github.io/Projeto_AdotaPet/. O backend Java e seu banco são executados localmente; login, administração e adoção reais não são oferecidos no site público.

## Resultado da triagem

**Situação no GitHub em 23/11/2026:** 12 Issues abertas, sendo **9 de validação pendente** (#2, #3, #4, #5, #6, #8, #11, #12, #19) e **3 de evolução futura** (#7, #13, #18). A Issue #16 foi encerrada anteriormente, após conferência das rotas e dos testes de rastreabilidade. A classificação representa a natureza das pendências, **não significa que o software ou os testes estejam falhando**. Nenhuma das demais foi encerrada sem comprovar todos os critérios de aceite.

| Issue | Tema | Situação na entrega |
| --- | --- | --- |
| [#2](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/2) | Catálogo público e filtros | **Validação pendente:** API com filtros e paginação e frontend presentes; conferir critérios integrados antes de encerrar |
| [#3](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/3) | Autocadastro, login e perfis | **Validação pendente:** `AuthController` e testes de API existem; falta validar a experiência completa com usuários locais |
| [#4](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/4) | Solicitações de adoção | **Validação pendente:** domínio, API e interface presentes; fluxo real local deve ser homologado |
| [#5](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/5) | Abrigos, animais e capacidade | **Validação pendente:** controladores e teste de capacidade presentes; conferir critérios completos |
| [#6](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/6) | Vacinas, adoção e pós-adoção | **Validação pendente:** entidades e testes de fluxo presentes; conferir operação local antes de encerrar |
| [#7](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/7) | Relatórios, Swagger, CI, LGPD | **Evolução futura:** documentação Swagger, relatórios e CI disponíveis; critérios mais amplos de LGPD e qualidade continuam na #13 |
| [#8](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/8) | Interface e acessibilidade | **Validação pendente:** visual desktop e mobile aprovado; faltam critérios completos de acessibilidade e fluxos locais |
| [#11](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/11) | Usuários, abrigos e voluntários | **Validação pendente:** controladores e `AdminIntegracaoTest` presentes; conferir critérios de ponta a ponta |
| [#12](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/12) | Painel da ONG | **Validação pendente:** painel e API disponíveis na versão local; verificar operações completas. O site público não oferece administração real |
| [#13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13) | Preparação para produção | **Evolução futura:** migrações, LGPD, segurança e testes adicionais; fora do escopo da vitrine acadêmica |
| [#16](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/16) | Matriz de endpoints / Swagger | **Fechada após conferência:** controladores e testes específicos em `RastreabilidadeApiTest`; melhorias avançadas seguem na #13 |
| [#18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18) | Fotos e upload de mídia | **Evolução futura:** campos `imagemDados`/`imagemMime` e controladores existem; consentimento, testes e uso real requerem validação |
| [#19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) | Homologação da integração | **Validação pendente:** `main` integrada e CI aprovada, mas não há verificação completa do backend local; prints dispensados nesta entrega |

## Conferência de código versus documentação

- O arquivo `docs/Diagrama de Classe.svg` retrata **seis grupos conceituais**; no código existem **oito classes @Entity**: `Usuario`, `Abrigo`, `Animal`, `Vacina`, `VacinacaoAnimal`, `SolicitacaoAdocao`, `Adocao`, `VisitaAcompanhamento`. Esta simplificação visual foi intencional e está explicada em `docs/Atualizacao-da-Modelagem.md`.
- O Dicionário de Dados e o Mapeamento JPA foram atualizados para refletir `imagem_dados` e `imagem_mime` de `Animal` e `Vacina`, que não estavam documentados.
- As rotas REST descritas na Issue #16 existem em `AnimalController`, `SolicitacaoController` e `AdocaoController`, e os testes focados constam de `RastreabilidadeApiTest`.
- Testes Java e fluxo de publicação no GitHub Pages passaram no commit `6a52b7f`. A publicação no Cloudflare Pages é gerenciada fora deste repositório; a responsável confirmou o funcionamento visual desktop/mobile em 22/11/2026.

## Entrega e limitações

Para avaliação: [site demonstrativo no GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/), [README](../README.md), [documentação acadêmica](ENTREGA_ACADEMICA_v1.0.0.md) e [guia de execução local](HOMOLOGACAO_LOCAL.md).

A responsável optou por **não reunir evidências em prints**. As Issues de validação pendente não exigem anexar capturas nesta entrega; podem ser verificadas posteriormente com execução local, testes e conferência funcional. A conferência de requisitos e a automação de CI documentadas aqui não implicam que o backend está liberado para usuários reais ou que todas as Issues foram concluídas. Em particular, segurança, privacidade, migrações de banco e homologação de produção permanecem fora do escopo da apresentação do protótipo.

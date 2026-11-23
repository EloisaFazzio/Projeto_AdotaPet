# Triagem das Issues — AdotaPet (entrega acadêmica, 22/11/2026)

**Objetivo:** diferenciar os requisitos já presentes no código das pendências para uso real. Esta triagem não substitui a conferência detalhada de todos os critérios de aceite, e **não exige prints ou anexos de evidência**, por orientação da responsável.

**Escopo do produto público:** demonstração estática com dados fictícios, disponibilizada pelo GitHub Pages em https://eloisafazzio.github.io/Projeto_AdotaPet/. O backend Java e seu banco são executados localmente; login, administração e adoção reais não são oferecidos no site público.

## Resultado da triagem

**Situação no GitHub em 23/11/2026, após confirmação funcional da responsável:** **9 Issues encerradas para o escopo acadêmico** (#2, #3, #4, #5, #6, #8, #11, #12 e #19); **3 permanecem abertas como evolução futura** (#7, #13 e #18). A Issue #16 já havia sido encerrada anteriormente. Os encerramentos têm comentários individuais indicando a confirmação de funcionamento, os testes automatizados e as limitações da homologação; **não comprovam uma auditoria independente exaustiva nem prontidão para produção**.

| Issue | Tema | Situação na entrega |
| --- | --- | --- |
| [#2](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/2) | Catálogo público e filtros | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#3](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/3) | Autocadastro, login e perfis | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#4](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/4) | Solicitações de adoção | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#5](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/5) | Abrigos, animais e capacidade | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#6](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/6) | Vacinas, adoção e pós-adoção | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#7](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/7) | Relatórios, Swagger, CI, LGPD | **Evolução futura:** documentação Swagger, relatórios e CI disponíveis; critérios mais amplos de LGPD e qualidade continuam na #13 |
| [#8](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/8) | Interface e acessibilidade | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#11](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/11) | Usuários, abrigos e voluntários | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#12](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/12) | Painel da ONG | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |
| [#13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13) | Preparação para produção | **Evolução futura:** migrações, LGPD, segurança e testes adicionais; fora do escopo da vitrine acadêmica |
| [#16](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/16) | Matriz de endpoints / Swagger | **Fechada após conferência:** controladores e testes específicos em `RastreabilidadeApiTest`; melhorias avançadas seguem na #13 |
| [#18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18) | Fotos e upload de mídia | **Evolução futura:** campos `imagemDados`/`imagemMime` e controladores existem; consentimento, testes e uso real requerem validação |
| [#19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) | Homologação da integração | **Concluída no escopo acadêmico:** funcionamento confirmado pela responsável; avaliação detalhada de produção fora do escopo |

## Conferência de código versus documentação

- O arquivo `docs/Diagrama de Classe.svg` retrata **seis grupos conceituais**; no código existem **oito classes @Entity**: `Usuario`, `Abrigo`, `Animal`, `Vacina`, `VacinacaoAnimal`, `SolicitacaoAdocao`, `Adocao`, `VisitaAcompanhamento`. Esta simplificação visual foi intencional e está explicada em `docs/Atualizacao-da-Modelagem.md`.
- O Dicionário de Dados e o Mapeamento JPA foram atualizados para refletir `imagem_dados` e `imagem_mime` de `Animal` e `Vacina`, que não estavam documentados.
- As rotas REST descritas na Issue #16 existem em `AnimalController`, `SolicitacaoController` e `AdocaoController`, e os testes focados constam de `RastreabilidadeApiTest`.
- Testes Java e fluxo de publicação no GitHub Pages passaram no commit `6a52b7f`. A publicação no Cloudflare Pages é gerenciada fora deste repositório; a responsável confirmou o funcionamento visual desktop/mobile em 22/11/2026.

## Entrega e limitações

Para avaliação: [site demonstrativo no GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/), [README](../README.md), [documentação acadêmica](ENTREGA_ACADEMICA_v1.0.0.md) e [guia de execução local](HOMOLOGACAO_LOCAL.md).

A responsável optou por **não reunir evidências em prints**. Ela confirmou o funcionamento do sistema baixado do GitHub, de modo que as nove Issues de validação foram encerradas quanto ao marco acadêmico. Testes futuros de carga, segurança, privacidade e funcionalidades reais em produção continuam fora desse encerramento. A conferência de requisitos e a automação de CI documentadas aqui não implicam que o backend está liberado para usuários reais ou que todas as Issues foram concluídas. Em particular, segurança, privacidade, migrações de banco e homologação de produção permanecem fora do escopo da apresentação do protótipo.

## Encerramento funcional informado pela responsável — 23/11/2026

A responsável confirmou em conversa que o AdotaPet baixado do GitHub estava funcional. Após essa confirmação, as Issues #2, #3, #4, #5, #6, #8, #11, #12 e #19 foram encerradas com justificativas individuais. Permanecem abertas exclusivamente as Issues #7, #13 e #18, para continuidade futura. Nenhuma captura de tela foi gerada por não fazer parte do escopo solicitado. A confirmação declarada pela responsável não representa auditoria independente do ambiente ou implantação de backend de produção.

# Triagem das Issues — AdotaPet (entrega acadêmica, 22/11/2026)

**Objetivo:** diferenciar os requisitos já presentes no código das pendências para uso real. Esta triagem não substitui a conferência detalhada de todos os critérios de aceite, e **não exige prints ou anexos de evidência**, por orientação da responsável.

**Escopo do produto público:** demonstração estática com dados fictícios, publicada em https://adotapet.pages.dev/. O backend Java e seu banco são executados localmente; login, administração e adoção reais não são oferecidos no site público.

## Resultado da triagem

| Issue | Tema | Situação na entrega |
| --- | --- | --- |
| [#2](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/2) | Catálogo público e filtros | Código de frontend e API disponível; deixar aberta até validar todos os critérios de aceite integrados |
| [#3](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/3) | Autocadastro, login e perfis | Backend presente; validação completa das permissões e operação local continua necessária |
| [#4](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/4) | Solicitações de adoção | Domínio, API e interface presentes; fluxo real completo ainda demanda homologação manual |
| [#5](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/5) | Abrigos, animais e capacidade | Implementações disponíveis; conferir critérios restantes antes do fechamento |
| [#6](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/6) | Vacinas, adoção e pós-adoção | Entidades, controladores e testes presentes; preservar para verificação abrangente |
| [#7](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/7) | Relatórios, Swagger, CI, LGPD | Escopo misto: parte acadêmica implementada; LGPD e produção permanecem na #13 |
| [#8](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/8) | Interface e acessibilidade | Versão desktop e mobile aprovada pela responsável; auditoria integral de acessibilidade não realizada |
| [#11](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/11) | Usuários, abrigos e voluntários | Controladores disponíveis; conferir todos os critérios de segurança e vínculo antes do fechamento |
| [#12](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/12) | Painel da ONG | Interface e backend disponíveis na versão local; o painel não funciona com dados reais na versão pública |
| [#13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13) | Preparação para produção | **Aberta:** migrações, LGPD, segurança e testes adicionais. Não necessária para o demonstrador acadêmico |
| [#16](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/16) | Matriz de endpoints / Swagger | **Fechada após conferência:** controladores e testes específicos em `RastreabilidadeApiTest`; melhorias avançadas seguem na #13 |
| [#18](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/18) | Fotos e upload de mídia | **Aberta parcialmente:** campos `imagemDados`/`imagemMime` e controladores existem; consentimento, testes e uso real requerem validação |
| [#19](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/19) | Homologação da integração | **Aberta:** `main` integrada e CI aprovada, mas não há verificação completa do backend local; prints dispensados nesta entrega |

## Conferência de código versus documentação

- O arquivo `docs/Diagrama de Classe.svg` retrata **seis grupos conceituais**; no código existem **oito classes @Entity**: `Usuario`, `Abrigo`, `Animal`, `Vacina`, `VacinacaoAnimal`, `SolicitacaoAdocao`, `Adocao`, `VisitaAcompanhamento`. Esta simplificação visual foi intencional e está explicada em `docs/Atualizacao-da-Modelagem.md`.
- O Dicionário de Dados e o Mapeamento JPA foram atualizados para refletir `imagem_dados` e `imagem_mime` de `Animal` e `Vacina`, que não estavam documentados.
- As rotas REST descritas na Issue #16 existem em `AnimalController`, `SolicitacaoController` e `AdocaoController`, e os testes focados constam de `RastreabilidadeApiTest`.
- Testes Java e fluxo de publicação no GitHub Pages passaram no commit `6a52b7f`. A publicação no Cloudflare Pages é gerenciada fora deste repositório; a responsável confirmou o funcionamento visual desktop/mobile em 22/11/2026.

## Entrega e limitações

Para avaliação: [site público](https://adotapet.pages.dev/), [README](../README.md), [documentação acadêmica](ENTREGA_ACADEMICA_v1.0.0.md) e [guia de execução local](HOMOLOGACAO_LOCAL.md).

A responsável optou por **não reunir evidências em prints**. A conferência de requisitos e a automação de CI documentadas aqui não implicam que o backend está liberado para usuários reais ou que todas as Issues foram concluídas. Em particular, segurança, privacidade, migrações de banco e homologação de produção permanecem fora do escopo da apresentação do protótipo.

# Dicionário de Dados — AdotaPet

**Projeto:** AdotaPet  
**Escopo:** entidades persistentes do domínio e tabelas relacionadas  
**Tipos:** tipos Java e tipos SQL abaixo são indicativos; o tipo SQL exato pode variar entre H2 e PostgreSQL e conforme o Hibernate.

## 1. Tabela `abrigo`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `nome` | String / VARCHAR(120) | Sim | Nome do abrigo; não pode ficar em branco |
| `endereco` | String / VARCHAR(200) | Sim | Endereço do abrigo |
| `cidade` | String / VARCHAR(80) | Sim | Cidade |
| `uf` | String / VARCHAR(2) | Sim | Unidade federativa |
| `telefone` | String / VARCHAR(20) | Não | Telefone de contato |
| `capacidade` | int / INTEGER | Sim | Capacidade máxima; validação mínima 1 |

**Relacionamentos:** um abrigo pode estar associado a vários animais; possui associação muitos-para-muitos com usuários que atuam como voluntários.

## 2. Tabela `usuario`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `nome` | String / VARCHAR(120) | Sim | Nome do usuário |
| `email` | String / VARCHAR(160) | Sim | E-mail válido e único |
| `senha_hash` | String / VARCHAR(100) | Sim | Hash da senha; nunca armazenar senha em texto puro |
| `telefone` | String / VARCHAR(20) | Não | Telefone de contato |
| `cidade` | String / VARCHAR(80) | Não | Cidade de residência |
| `perfil` | Enum / VARCHAR(20) | Sim | `ADMIN`, `VOLUNTARIO` ou `ADOTANTE`; persistido como texto |
| `ativo` | boolean / BOOLEAN | Sim | Indica se a conta está ativa; padrão inicial `true` |

**Relacionamentos:** um usuário adotante pode registrar várias solicitações; um usuário voluntário pode estar vinculado a vários abrigos; usuários também podem aparecer como analistas de solicitações e voluntários em visitas.

## 3. Tabela `animal`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `nome` | String / VARCHAR(80) | Sim | Nome do animal |
| `especie` | Enum / VARCHAR(20) | Sim | `CAO`, `GATO` ou `OUTRA` |
| `raca` | String / VARCHAR(80) | Não | Raça informada |
| `sexo` | Enum / VARCHAR(10) | Sim | `MACHO` ou `FEMEA` |
| `porte` | Enum / VARCHAR(10) | Sim | `PEQUENO`, `MEDIO` ou `GRANDE` |
| `data_nascimento` | LocalDate / DATE | Não | Data de nascimento, quando conhecida |
| `data_resgate` | LocalDate / DATE | Sim | Data de resgate; padrão inicial é a data atual |
| `descricao` | String / VARCHAR(1000) | Não | Informações adicionais sobre o animal |
| `castrado` | boolean / BOOLEAN | Sim | Indica se o animal é castrado; padrão Java `false` |
| `status` | Enum / VARCHAR(25) | Sim | `DISPONIVEL`, `EM_TRATAMENTO`, `EM_PROCESSO_ADOCAO` ou `ADOTADO`; padrão inicial `DISPONIVEL` |
| `abrigo_id` | Long / BIGINT | Sim | FK para `abrigo.id` |

**Relacionamentos:** pertence a um abrigo; pode ter várias vacinações e solicitações de adoção.

## 4. Tabela `vacina`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `nome` | String / VARCHAR(100) | Sim | Nome da vacina; único |
| `especie_alvo` | Enum / VARCHAR(20) | Sim | Espécie à qual a vacina se destina; persistida como texto |
| `intervalo_dias` | Integer / INTEGER | Não | Intervalo recomendado em dias; nulo pode representar dose única |

**Relacionamentos:** pode estar vinculada a vários registros de vacinação.

## 5. Tabela `vacinacao_animal`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `animal_id` | Long / BIGINT | Sim | FK para `animal.id` |
| `vacina_id` | Long / BIGINT | Sim | FK para `vacina.id` |
| `data_aplicacao` | LocalDate / DATE | Sim | Data em que a vacina foi aplicada |
| `proxima_dose` | LocalDate / DATE | Não | Data prevista para a próxima dose |
| `veterinario` | String / VARCHAR(120) | Não | Nome ou identificação do veterinário |

**Observação:** a regra de domínio exige que a próxima dose, quando informada, seja posterior à data de aplicação.

## 6. Tabela `solicitacao_adocao`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `adotante_id` | Long / BIGINT | Sim | FK para `usuario.id` |
| `animal_id` | Long / BIGINT | Sim | FK para `animal.id` |
| `analisado_por_id` | Long / BIGINT | Não | FK para `usuario.id`; nulo enquanto não analisada |
| `justificativa` | String / VARCHAR(1000) | Sim | Justificativa apresentada pelo adotante |
| `parecer` | String / VARCHAR(1000) | Não | Parecer registrado na análise |
| `status` | Enum / VARCHAR(20) | Sim | `PENDENTE`, `EM_ANALISE`, `APROVADA`, `REJEITADA` ou `CANCELADA`; padrão inicial `PENDENTE` |
| `solicitada_em` | LocalDateTime / TIMESTAMP | Sim | Data e hora do registro; padrão inicial é o momento de criação |
| `analisada_em` | LocalDateTime / TIMESTAMP | Não | Data e hora da conclusão da análise |

**Relacionamentos:** pertence a um adotante e a um animal; pode ter um analista e pode originar uma adoção formalizada.

## 7. Tabela `adocao`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `solicitacao_id` | Long / BIGINT | Sim | FK única para `solicitacao_adocao.id` |
| `data_adocao` | LocalDate / DATE | Sim | Data da adoção; padrão inicial é a data atual |
| `termo_assinado` | boolean / BOOLEAN | Sim | Indica a assinatura do termo de responsabilidade |
| `observacoes` | String / VARCHAR(500) | Não | Observações sobre a formalização |

**Relacionamentos:** uma adoção está associada a uma solicitação; pode possuir várias visitas de acompanhamento.

## 8. Tabela `visita_acompanhamento`

| Campo | Tipo lógico | Obrigatório | Restrições / descrição |
| --- | --- | --- | --- |
| `id` | Long / BIGINT | Sim | Chave primária, gerada automaticamente |
| `adocao_id` | Long / BIGINT | Sim | FK para `adocao.id` |
| `voluntario_id` | Long / BIGINT | Sim | FK para `usuario.id` |
| `data_visita` | LocalDate / DATE | Sim | Data da visita ou contato |
| `observacoes` | String / VARCHAR(1000) | Sim | Registro do acompanhamento |
| `animal_bem_cuidado` | boolean / BOOLEAN | Sim | Avaliação dos cuidados; padrão inicial `true` |

## 9. Tabela de associação `voluntario_abrigo`

| Campo | Tipo lógico | Obrigatório | Descrição |
| --- | --- | --- | --- |
| `usuario_id` | Long / BIGINT | Sim | Referência ao usuário associado ao abrigo |
| `abrigo_id` | Long / BIGINT | Sim | Referência ao abrigo associado ao usuário |

A tabela é configurada por `@JoinTable` em `Usuario.abrigos`. A definição explícita de uma chave primária composta não aparece no código atual.

## 10. Enumerações

| Enumeração | Valores permitidos |
| --- | --- |
| `Perfil` | `ADMIN`, `VOLUNTARIO`, `ADOTANTE` |
| `Especie` | `CAO`, `GATO`, `OUTRA` |
| `Sexo` | `MACHO`, `FEMEA` |
| `Porte` | `PEQUENO`, `MEDIO`, `GRANDE` |
| `StatusAnimal` | `DISPONIVEL`, `EM_TRATAMENTO`, `EM_PROCESSO_ADOCAO`, `ADOTADO` |
| `StatusSolicitacao` | `PENDENTE`, `EM_ANALISE`, `APROVADA`, `REJEITADA`, `CANCELADA` |

## 11. Segurança e qualidade dos dados

- O campo `senha_hash` deve armazenar somente o hash da senha, nunca a senha original.
- Dados pessoais devem ser acessados somente por perfis autorizados e tratados de acordo com a LGPD.
- Restrições de negócio que envolvam múltiplos registros — como impedir solicitações ativas duplicadas ou rejeitar solicitações concorrentes — devem ser protegidas pela camada de serviço e, quando apropriado, por restrições transacionais no banco.
- Este dicionário descreve o mapeamento encontrado no código e deve ser atualizado quando entidades, anotações ou regras forem modificadas.

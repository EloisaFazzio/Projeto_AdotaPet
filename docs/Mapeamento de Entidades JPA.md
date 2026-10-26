# Mapeamento de Entidades JPA — AdotaPet

**Projeto:** AdotaPet  
**Tecnologia:** Java 17, Jakarta Persistence (JPA), Spring Data JPA e Hibernate  
**Objetivo:** documentar como as entidades do domínio são mapeadas para tabelas e relacionamentos relacionais.

> Este documento foi elaborado a partir das anotações JPA presentes nas classes do repositório. O esquema físico final pode variar conforme o banco utilizado e a estratégia de geração de DDL do Hibernate.

## 1. Convenções gerais

- Cada classe anotada com `@Entity` representa uma entidade persistente.
- As chaves primárias usam `@Id` e `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Enumerações são armazenadas como texto por meio de `@Enumerated(EnumType.STRING)`.
- Relacionamentos que possuem `@JoinColumn` definem a coluna de chave estrangeira.
- Campos com `nullable = false` são obrigatórios no mapeamento JPA.
- As anotações de validação, como `@NotBlank`, `@NotNull`, `@Email` e `@Min`, complementam as restrições de persistência; a validação depende de ser acionada pela aplicação.

## 2. Entidades e tabelas

| Entidade Java | Tabela | Chave primária |
| --- | --- | --- |
| `Abrigo` | `abrigo` | `id` |
| `Usuario` | `usuario` | `id` |
| `Animal` | `animal` | `id` |
| `Vacina` | `vacina` | `id` |
| `VacinacaoAnimal` | `vacinacao_animal` | `id` |
| `SolicitacaoAdocao` | `solicitacao_adocao` | `id` |
| `Adocao` | `adocao` | `id` |
| `VisitaAcompanhamento` | `visita_acompanhamento` | `id` |

## 3. Mapeamento de relacionamentos

| Relacionamento | Mapeamento JPA | Implementação no banco |
| --- | --- | --- |
| Abrigo — Animal | `Abrigo.animais` com `@OneToMany(mappedBy="abrigo")`; `Animal.abrigo` com `@ManyToOne` | FK `animal.abrigo_id` |
| Usuário — Abrigo | `Usuario.abrigos` com `@ManyToMany`; lado inverso em `Abrigo.voluntarios` | Tabela de junção `voluntario_abrigo`, colunas `usuario_id` e `abrigo_id` |
| Animal — Vacinação | `Animal.vacinacoes` com `@OneToMany`; `VacinacaoAnimal.animal` com `@ManyToOne` | FK `vacinacao_animal.animal_id` |
| Vacina — Vacinação | `Vacina.aplicacoes` com `@OneToMany`; `VacinacaoAnimal.vacina` com `@ManyToOne` | FK `vacinacao_animal.vacina_id` |
| Usuário adotante — Solicitação | `Usuario.solicitacoes` com `@OneToMany(mappedBy="adotante")`; lado proprietário em `SolicitacaoAdocao.adotante` | FK `solicitacao_adocao.adotante_id` |
| Animal — Solicitação | `Animal.solicitacoes` com `@OneToMany`; `SolicitacaoAdocao.animal` com `@ManyToOne` | FK `solicitacao_adocao.animal_id` |
| Usuário analista — Solicitação | `SolicitacaoAdocao.analisadoPor` com `@ManyToOne` opcional | FK `solicitacao_adocao.analisado_por_id`, nula antes da análise |
| Solicitação — Adoção | `Adocao.solicitacao` com `@OneToOne` e `unique = true`; lado inverso em `SolicitacaoAdocao.adocao` | FK única `adocao.solicitacao_id` |
| Adoção — Visitas | `Adocao.visitas` com `@OneToMany`, `cascade = ALL` e `orphanRemoval = true`; `VisitaAcompanhamento.adocao` com `@ManyToOne` | FK `visita_acompanhamento.adocao_id` |
| Usuário voluntário — Visitas | `VisitaAcompanhamento.voluntario` com `@ManyToOne` | FK `visita_acompanhamento.voluntario_id` |

## 4. Regras de persistência relevantes

- O e-mail de `Usuario` é obrigatório e único.
- O nome de `Vacina` é obrigatório e único.
- Uma adoção referencia uma solicitação por meio de uma relação um-para-um com chave estrangeira única.
- Os campos de enumeração são armazenados pelos nomes dos valores, o que facilita a leitura dos dados, mas exige cuidado ao renomear constantes.
- `Animal.vacinacoes` e `Adocao.visitas` usam cascata `ALL` e remoção de órfãos; a remoção de uma entidade pai pode propagar operações aos registros filhos conforme o mapeamento.
- `spring.jpa.hibernate.ddl-auto=update` está configurado no arquivo `application.properties`; em produção, recomenda-se avaliar migrações controladas de banco de dados.

## 5. Observações

1. A tabela `voluntario_abrigo` é criada pela configuração `@JoinTable`; não existe uma entidade Java separada para ela.
2. As entidades não definem nomes explícitos para todas as colunas simples. Nesses casos, o nome físico depende da estratégia de nomenclatura do Hibernate.
3. Este documento descreve o mapeamento atual do código, não substitui migrações versionadas nem uma inspeção do esquema real do banco.

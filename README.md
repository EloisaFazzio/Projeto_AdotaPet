# AdotaPet 🐾

> Plataforma web para apoiar ONGs de proteção animal na gestão de abrigos, animais, vacinação e processos de adoção.

O **AdotaPet** é um projeto acadêmico de Engenharia de Software desenvolvido para centralizar informações e tornar o processo de adoção mais organizado, rastreável e seguro — do acolhimento do animal ao acompanhamento pós-adoção.

## 📌 Status do projeto

**Etapa atual:** implementação incremental full stack proposta nos Pull Requests #9 (API) e #10 (interface).

O repositório mantém oito entidades JPA e os repositórios correspondentes. A evolução de API REST e frontend está em branches separadas para revisão. Consulte `docs/IMPLEMENTACAO_FULLSTACK.md` para os endpoints implementados e as funcionalidades ainda em desenvolvimento.

## 🎯 Objetivos

- Centralizar o cadastro de abrigos, voluntários, adotantes e animais.
- Consultar animais disponíveis com filtros.
- Manter o catálogo de vacinas e o histórico de vacinação.
- Registrar, analisar e acompanhar solicitações de adoção.
- Formalizar adoções com termo de responsabilidade.
- Registrar visitas ou contatos de acompanhamento pós-adoção.
- Apoiar a gestão da ONG com informações e relatórios.

## 👥 Perfis de usuário

| Perfil | Responsabilidades previstas |
| --- | --- |
| **Administrador (ADMIN)** | Gerenciar usuários, abrigos, vacinas e relatórios. |
| **Voluntário (VOLUNTARIO)** | Cadastrar animais, registrar vacinações, analisar solicitações e realizar acompanhamentos. |
| **Adotante (ADOTANTE)** | Consultar animais disponíveis e solicitar adoção. |

## 🧰 Tecnologias

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Data JPA / Hibernate**
- **Maven**
- **H2** para execução local por padrão
- **PostgreSQL** para ambiente configurado, incluindo Supabase
- **Bean Validation**
- **JUnit / Spring Boot Test** para testes

## 🏗️ Estrutura do projeto

```text
src/
└── main/
    ├── java/br/edu/catolicasc/adotapet/
    │   ├── AdotaPetApplication.java
    │   ├── model/
    │   │   └── enums/
    │   └── repository/
    └── resources/
        └── application.properties
docs/
pom.xml
README.md
```

### Modelo de domínio

O modelo possui oito entidades JPA independentes:
- `Usuario`
- `Abrigo`
- `Animal`
- `Vacina`
- `VacinacaoAnimal`
- `SolicitacaoAdocao`
- `Adocao`
- `VisitaAcompanhamento`

Para facilitar a leitura, o diagrama agrupa visualmente as responsabilidades em seis blocos conceituais: **Abrigo**, **Animal**, **Vacinação do Animal**, **Usuário**, **Processo de Adoção** e **Acompanhamento Pós-Adoção**. 

## 📋 Regras de negócio consideradas

- Somente animais com status **DISPONIVEL** podem receber solicitações.
- Um adotante não pode manter solicitações ativas duplicadas para o mesmo animal.
- A análise e a decisão sobre solicitações dependem do perfil autorizado.
- A aprovação de uma solicitação deve tratar as demais solicitações ativas do mesmo animal.
- A adoção só pode ser formalizada após aprovação e assinatura do termo de responsabilidade.
- O limite de capacidade do abrigo deve ser respeitado.
- A próxima dose, quando informada, deve ser posterior à data de aplicação.
- O acesso a dados pessoais deve respeitar os princípios de privacidade e a LGPD.

> As regras acima fazem parte da especificação do sistema; nem todas estão implementadas na camada de serviço/API nesta etapa.

## 🚀 Como executar localmente

### Pré-requisitos

- JDK **17**
- Maven **3.9 ou superior**

Confira as instalações:

```bash
java -version
mvn -version
```

Na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

Por padrão, a aplicação utiliza um banco **H2 em memória** no modo de compatibilidade com PostgreSQL. A configuração é definida em `src/main/resources/application.properties`.

### Usar PostgreSQL ou Supabase

Configure as variáveis de ambiente antes de iniciar a aplicação:

| Variável | Descrição |
| --- | --- |
| `DB_URL` | URL JDBC do banco PostgreSQL |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |

Exemplo de formato para `DB_URL`:

```text
jdbc:postgresql://<host>:5432/<database>
```

Não inclua senhas, tokens ou outras credenciais no repositório. Use variáveis de ambiente e mantenha os dados de acesso privados.

## 🖥️ Interface web e integração

- A branch `feat/frontend-integrado-adotapet` contém a interface responsiva integrada às rotas REST.
- A branch `feat/api-catalogo-autenticacao-adocao` fornece os controllers, autenticação, autorizações e fluxo de adoção.
- O frontend deve ser integrado **após o backend**; não há animais pré-cadastrados para evitar demonstrações confundidas com dados reais.
- Consulte as Issues abertas para acompanhar etapas pendentes, como gestão completa de usuários, hardening de segurança e testes ponta-a-ponta.

## 📚 Documentação

A documentação acadêmica da Etapa 1 está organizada na pasta [`docs/`](docs/).

- **[Diagrama de Classe](docs/Diagrama%20de%20Classe.svg)** — visão conceitual das principais responsabilidades do domínio.
- **[Atualização da Modelagem](docs/Atualizacao-da-Modelagem.md)** — explica os agrupamentos do diagrama e sua compatibilidade com o modelo persistente.
- **[Mapeamento de Entidades JPA](docs/Mapeamento%20de%20Entidades%20JPA.md)** — entidades, tabelas, chaves e relacionamentos.
- **[Dicionário de Dados](docs/Dicion%C3%A1rio%20de%20Dados.md)** — campos, tipos, obrigatoriedade e restrições.
- **[Implementação full stack](docs/IMPLEMENTACAO_FULLSTACK.md)** — endpoints, execução e limitações da Etapa 2. O PDF acadêmico da Etapa 1 foi entregue separadamente e ainda não está versionado neste repositório.

## 👩‍💻 Equipe

| Integrante | Função |
| --- | --- |
| Fabrício da Silva | Integrante |
| Eloisa Fazzio da Silva Rocha | Líder da equipe |
| Eric Eduardo Lange | Integrante |

## 🎓 Contexto acadêmico

Projeto Final — Etapa 1, do curso de **Engenharia de Software** do Centro Universitário Católica de Santa Catarina, em Jaraguá do Sul (2026).

---

Desenvolvido com o propósito de apoiar a adoção responsável e o bem-estar animal. 💚

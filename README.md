# AdotaPet 🐾

> Plataforma web para apoiar ONGs de proteção animal na gestão de abrigos, animais, vacinação e processos de adoção.

O **AdotaPet** é um projeto acadêmico de Engenharia de Software desenvolvido para centralizar informações e tornar o processo de adoção mais organizado, rastreável e seguro — do acolhimento do animal ao acompanhamento pós-adoção.

## 📌 Status do projeto

**Etapa atual:** modelagem, requisitos e estrutura inicial do backend.

O repositório contém as entidades de domínio, os repositórios Spring Data JPA e a configuração inicial da aplicação. A interface web está disponível na branch `feat/site-adotapet` como protótipo visual; seus dados e formulários ainda não estão integrados à API.

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

O modelo prevê oito entidades principais:

- `Usuario`
- `Abrigo`
- `Animal`
- `VacinacaoAnimal`
- `Processodeadoção`
- `VisitapósAdoção`

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

## 🖥️ Protótipo da interface

A branch [`feat/site-adotapet`](https://github.com/EloisaFazzio/Projeto_AdotaPet/tree/feat/site-adotapet) contém uma página inicial responsiva em `src/main/resources/static/index.html`.

O protótipo inclui apresentação do projeto, catálogo demonstrativo de animais, filtros, favoritos locais e formulários ilustrativos. **Os dados são de demonstração e os formulários ainda não enviam informações para o backend.** Para consultar o protótipo, abra o arquivo `index.html` em um navegador.

## 📚 Documentação

A documentação acadêmica da Etapa 1 está organizada na pasta [`docs/`](docs/).

- **[Diagrama de Classe](docs/Diagrama%20de%20Classe.svg)** — visão conceitual das principais responsabilidades do domínio.
- **[Atualização da Modelagem](docs/Atualizacao-da-Modelagem.md)** — explica os agrupamentos do diagrama e sua compatibilidade com o modelo persistente.
- **[Mapeamento de Entidades JPA](docs/Mapeamento%20de%20Entidades%20JPA.md)** — entidades, tabelas, chaves e relacionamentos.
- **[Dicionário de Dados](docs/Dicion%C3%A1rio%20de%20Dados.md)** — campos, tipos, obrigatoriedade e restrições.
- **[Documentação acadêmica — Etapa 1 (`PROJETO_ADOTAPET_PARTE_01.docx`)](docs/PROJETO_ADOTAPET_PARTE_01.docx)** — documento acadêmico de referência.

## 👩‍💻 Equipe

| Integrante | Função |
| --- | --- |
| Fabrício da Silva | Líder da equipe |
| Eloisa Fazzio da Silva Rocha | Integrante |
| Eric Eduardo Lange | Integrante |

## 🎓 Contexto acadêmico

Projeto Final — Etapa 1, do curso de **Engenharia de Software** do Centro Universitário Católica de Santa Catarina, em Jaraguá do Sul (2026).

---

Desenvolvido com o propósito de apoiar a adoção responsável e o bem-estar animal. 💚

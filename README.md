# AdotaPet 🐾

> Plataforma web para apoiar ONGs de proteção animal na gestão de abrigos, animais, vacinação e processos de adoção.

O **AdotaPet** é um projeto acadêmico de Engenharia de Software desenvolvido para centralizar informações e tornar o processo de adoção mais organizado, rastreável e seguro — do acolhimento do animal ao acompanhamento pós-adoção.

## 📌 Status do projeto

**Etapa atual:** homologação da versão integrada no **[Pull Request #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20)**; os PRs funcionais permanecem independentes e a branch `main` não foi alterada.

A branch `feat/integracao-homologacao-adotapet` reúne **site responsivo, painel da ONG, API Java, autenticação, cadastro de animais, vacinação, fluxo de adoção e relatórios**. Possui 19 testes Java e CI de integração HTTP com H2; consulte o [guia de homologação local](docs/HOMOLOGACAO_LOCAL.md). O código ainda requer revisão e testes manuais antes de produção.

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
    │   ├── repository/
    │   ├── api/
    │   └── security/
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── app.js
            ├── styles.css
            ├── painel.html
            ├── painel.js
            └── painel.css
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

> A branch de homologação implementa os principais fluxos e verifica os cenários centrais na CI; permanecem pendentes os testes completos em navegador, migrações versionadas, políticas de privacidade e hardening para produção.

## 🚀 Como executar localmente

**Recomendado no Windows:** [Guia com Docker Compose, PostgreSQL e conta administradora](docs/HOMOLOGACAO_LOCAL.md). Para começar, crie seu arquivo `.env` a partir de `.env.example`, configure credenciais privadas e execute `docker compose up --build -d` na branch de homologação.

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

- **Site público:** http://localhost:8080 (animais disponíveis, filtros, login e solicitações).
- **Painel da ONG:** http://localhost:8080/painel.html (gestão de animais, abrigos, vacinação, pedidos, visitas e relatórios).
- **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html (somente em desenvolvimento).
- A integração dos PRs #9, #14, #17, #10 e #15 foi consolidada no PR #20. O catálogo não usa registros fictícios por padrão; a equipe precisa cadastrar animais e abrigos.
- Os testes de API e de HTTP foram automatizados. A revisão visual em navegador e os requisitos de produção ainda estão pendentes.

## 📚 Documentação

A documentação acadêmica da Etapa 1 está organizada na pasta [`docs/`](docs/).

- **[Diagrama de Classe](docs/Diagrama%20de%20Classe.svg)** — visão conceitual das principais responsabilidades do domínio.
- **[Atualização da Modelagem](docs/Atualizacao-da-Modelagem.md)** — explica os agrupamentos do diagrama e sua compatibilidade com o modelo persistente.
- **[Mapeamento de Entidades JPA](docs/Mapeamento%20de%20Entidades%20JPA.md)** — entidades, tabelas, chaves e relacionamentos.
- **[Dicionário de Dados](docs/Dicion%C3%A1rio%20de%20Dados.md)** — campos, tipos, obrigatoriedade e restrições.
- **[Implementação full stack](docs/IMPLEMENTACAO_FULLSTACK.md)** — endpoints e etapas da API.
- **[Painel da ONG](docs/PAINEL_ONG.md)** — operações administrativas e limitações.
- **[Rastreabilidade e Swagger](docs/API_RASTREABILIDADE_E_SWAGGER.md)** — endpoints adicionais da matriz.
- **[Homologação local](docs/HOMOLOGACAO_LOCAL.md)** — como iniciar o site completo com Docker Compose no Windows. O PDF acadêmico da Etapa 1 foi entregue separadamente e ainda não está versionado neste repositório.

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

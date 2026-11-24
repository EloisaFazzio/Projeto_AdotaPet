# AdotaPet 🐾

> Plataforma web para apoiar ONGs de proteção animal na gestão de abrigos, animais, vacinação e processos de adoção.

O **AdotaPet** é um projeto acadêmico de Engenharia de Software desenvolvido para centralizar informações e tornar o processo de adoção mais organizado, rastreável e seguro — do acolhimento do animal ao acompanhamento pós-adoção.

## 📌 Status do projeto

**Versão acadêmica:** [v1.0.0-academica](https://github.com/EloisaFazzio/Projeto_AdotaPet/releases/tag/v1.0.0-academica). Mantemos o código atualizado na branch **`main`**.

### 🌐 Demonstração online gratuita

**🌐 [Acessar a demonstração do AdotaPet — GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)**

Disponibilizamos a demonstração do AdotaPet no **[GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)**. Para executar o sistema completo, utilizamos o [guia de homologação local](docs/HOMOLOGACAO_LOCAL.md).

### 🖥️ Sistema completo local

O **Spring Boot 3.3.5 + Java 17 + PostgreSQL/H2** está disponível na `main`: site responsivo, painel administrativo, API REST, autenticação, cadastro de animais, vacinação, adoção e relatórios. Execute localmente seguindo o [guia de homologação](docs/HOMOLOGACAO_LOCAL.md). A CI cobre testes Java e integração HTTP, mas o sistema **não foi liberado para produção pública com dados pessoais**. Antes disso, faltam revisões de segurança, LGPD, migrações de banco e testes E2E completos (ver [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13)).

## 🎯 Objetivos

- Centralizar o cadastro de abrigos, adotantes e animais.
- Consultar animais disponíveis com filtros.
- Manter o catálogo de vacinas e o histórico de vacinação.
- Registrar, analisar e acompanhar solicitações de adoção.
- Formalizar adoções com termo de responsabilidade.
- Registrar visitas ou contatos de acompanhamento pós-adoção.
- Apoiar a gestão da ONG com informações e relatórios.

## 👥 Perfis de usuário apresentados

| Perfil | Responsabilidades previstas |
| --- | --- |
| **Administrador (ADMIN)** | Gerenciar usuários, abrigos, vacinas e relatórios. |
| **Adotante (ADOTANTE)** | Consultar animais disponíveis e solicitar adoção. |

## 🧰 Tecnologias

**Publicação da demonstração:** GitHub Pages, com os arquivos de [`site-demo/`](site-demo/) publicados pelo workflow `.github/workflows/pages-vitrine.yml`.


- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Data JPA / Hibernate**
- **Maven**
- **Docker Desktop / Docker Compose** para homologação com PostgreSQL
- **HTML, CSS e JavaScript** no site público e no painel da ONG
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

> A versão integrada está na `main`. Testes completos em navegador, migrações versionadas, políticas de privacidade e endurecimento de segurança são necessários antes de qualquer uso público com dados reais.

## 🚀 Como executar localmente

**Recomendado no Windows:** [Guia com Docker Compose, PostgreSQL e conta administradora](docs/HOMOLOGACAO_LOCAL.md). Para começar, crie seu arquivo `.env` a partir de `.env.example`, configure credenciais privadas e execute `docker compose up --build -d` na branch `main`.

### Pré-requisitos

- JDK **17**
- Maven **3.9 ou superior** (para execução fora dos contêineres)
- Docker Desktop com Docker Compose (para a instalação recomendada com PostgreSQL)

Confira as instalações:

```bash
java -version
mvn -version
```

Na raiz do projeto, para executar **sem Docker**, use:

```powershell
mvn spring-boot:run
```

Por padrão, a aplicação utiliza um banco **H2 em memória** no modo de compatibilidade com PostgreSQL. A configuração é definida em `src/main/resources/application.properties`. Nesse modo, os cadastros não são preservados após encerrar a aplicação. Para testar com dados persistentes e conta administradora, utilize o procedimento com Docker Compose e `.env` descrito no [guia de homologação](docs/HOMOLOGACAO_LOCAL.md).

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


## ✅ Baixar e executar o AdotaPet do GitHub (Windows)

Qualquer pessoa pode baixar o **código-fonte completo** da branch `main` para estudar e executar o projeto localmente. O download inclui frontend, backend Spring Boot, testes e documentação. **Não inclui os registros de um banco PostgreSQL já utilizado, credenciais ou arquivos privados**: cada pessoa inicia seu próprio ambiente.

### Instalação recomendada — Docker Desktop

1. Acesse [o repositório no GitHub](https://github.com/EloisaFazzio/Projeto_AdotaPet), selecione a branch `main` e clique em **Code → Download ZIP**.
2. Extraia o ZIP e **abra a pasta que contém `pom.xml`, `compose.yaml` e `.env.example`**. Algumas extrações criam uma pasta `Projeto_AdotaPet-main` dentro de outra.
3. Abra o PowerShell nessa pasta e crie seu arquivo local de configuração:

   ```powershell
   Copy-Item .env.example .env
   notepad .env
   ```

4. Preencha `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `ADOTAPET_ADMIN_EMAIL`, `ADOTAPET_ADMIN_PASSWORD` e, se desejar, `ADOTAPET_ADMIN_NAME` com **valores próprios e privados**. O [guia de homologação](docs/HOMOLOGACAO_LOCAL.md) explica como gerar o segredo JWT e as exigências das credenciais.
5. Abra o Docker Desktop e, no PowerShell, execute:

   ```powershell
   docker compose up --build -d
   docker compose logs -f aplicacao
   ```

6. Quando a aplicação iniciar, acesse **http://localhost:8080** (site) ou **http://localhost:8080/painel.html** (painel administrativo). Use as credenciais de administrador criadas no `.env`. Para encerrar a visualização dos logs, pressione **Ctrl+C**; os contêineres continuam em execução.

**Banco de dados e catálogo:** o Docker Compose cria um PostgreSQL local independente, inicialmente sem os animais e abrigos de outra instalação. Cadastre registros fictícios no painel para realizar os testes. O código também contém `DemoDataBootstrap`, que oferece uma carga fictícia **opcional e desativada por padrão**; ela só deve ser ativada em ambiente isolado de desenvolvimento, conforme [guia e configuração do projeto](docs/HOMOLOGACAO_LOCAL.md). As imagens estáticas incluídas no repositório são diferentes dos registros do banco.

**Porta utilizada:** `8080` é a porta padrão do projeto. Não é necessário configurar portas alternativas ou restaurar backups para uma instalação normal. Se a porta `8080` já estiver ocupada por outra instalação, pare a aplicação anterior antes de iniciar a cópia — não execute as duas instâncias simultaneamente na mesma porta.

**Testes automatizados:** com Java 17 e Maven instalados no computador, execute `mvn test` na raiz do projeto. Os testes também são verificados pelo GitHub Actions.


> **Distinção importante:** https://eloisafazzio.github.io/Projeto_AdotaPet/ é uma demonstração pública estática com dados fictícios; o download do GitHub permite executar o sistema Java completo **localmente**, mediante a configuração do ambiente e do banco de dados. A aplicação ainda não está homologada para uso público com dados pessoais reais.

## 🖥️ Interface web e integração

- **Demonstração pública do repositório (somente frontend):** https://eloisafazzio.github.io/Projeto_AdotaPet/
- **Site público:** http://localhost:8080 (animais disponíveis, filtros, login e solicitações).
- **Painel da ONG:** http://localhost:8080/painel.html (gestão de animais, abrigos, vacinação, pedidos, visitas e relatórios).
- **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html (somente em desenvolvimento).
- A integração dos PRs #9, #14, #17, #10 e #15 foi incorporada à `main` pelo [PR #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20).

## 📚 Documentação

**Para avaliação:** acesse [a demonstração do AdotaPet no GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/), consulte a [entrega acadêmica](docs/ENTREGA_ACADEMICA_v1.0.0.md) e execute o backend local usando o [guia de homologação](docs/HOMOLOGACAO_LOCAL.md). O ambiente online não executa Java nem permite adoções reais.


A documentação acadêmica da Etapa 1 está organizada na pasta [`docs/`](docs/).

- **[Diagrama de Classe](docs/Diagrama%20de%20Classe.svg)** — visão conceitual das principais responsabilidades do domínio.
- **[Atualização da Modelagem](docs/Atualizacao-da-Modelagem.md)** — explica os agrupamentos do diagrama e sua compatibilidade com o modelo persistente.
- **[Mapeamento de Entidades JPA](docs/Mapeamento%20de%20Entidades%20JPA.md)** — entidades, tabelas, chaves e relacionamentos.
- **[Dicionário de Dados](docs/Dicion%C3%A1rio%20de%20Dados.md)** — campos, tipos, obrigatoriedade e restrições.
- **[Implementação full stack](docs/IMPLEMENTACAO_FULLSTACK.md)** — endpoints e etapas da API.
- **[Painel da ONG](docs/PAINEL_ONG.md)** — operações administrativas e limitações.
- **[Rastreabilidade e Swagger](docs/API_RASTREABILIDADE_E_SWAGGER.md)** — endpoints adicionais da matriz.
- **[Homologação local](docs/HOMOLOGACAO_LOCAL.md)** — como iniciar o site completo com Docker Compose no Windows.
- **[Auditoria de arquivos e entrega](docs/AUDITORIA_FINAL.md)** — classificação de arquivos ativos, redundantes e possíveis resíduos.
- **[Triagem das Issues da entrega](docs/TRIAGEM_ISSUES_ENTREGA.md)** — o que foi verificado, o que permanece aberto e os limites do protótipo acadêmico.
- **[Marco da entrega acadêmica v1.0.0](docs/ENTREGA_ACADEMICA_v1.0.0.md)** — resumo da versão, critérios atendidos e limitações. O PDF acadêmico da Etapa 1 foi entregue separadamente e ainda não está versionado neste repositório.

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

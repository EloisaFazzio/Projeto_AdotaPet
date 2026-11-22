# AdotaPet 🐾

> Plataforma web para apoiar ONGs de proteção animal na gestão de abrigos, animais, vacinação e processos de adoção.

O **AdotaPet** é um projeto acadêmico de Engenharia de Software desenvolvido para centralizar informações e tornar o processo de adoção mais organizado, rastreável e seguro — do acolhimento do animal ao acompanhamento pós-adoção.

## 📌 Status do projeto

**Versão:** entrega acadêmica integrada (2026). O código completo foi incorporado à branch **`main`** por meio do [Pull Request #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20).

### 🌐 Demonstração online gratuita

**[Abrir o AdotaPet no GitHub Pages](https://eloisafazzio.github.io/Projeto_AdotaPet/)**

A versão hospedada gratuitamente publica o **frontend visual original** com dados fictícios, sem banco de dados, login, cadastros ou solicitações reais. Os arquivos de publicação ficam em [`site-demo/`](site-demo/); os arquivos do sistema completo continuam em `src/main/resources/static/`.

### 🖥️ Sistema completo local

O **Spring Boot 3.3.5 + Java 17 + PostgreSQL/H2** está disponível na `main`: site responsivo, painel administrativo, API REST, autenticação, cadastro de animais, vacinação, adoção e relatórios. Execute localmente seguindo o [guia de homologação](docs/HOMOLOGACAO_LOCAL.md). A CI cobre testes Java e integração HTTP, mas o sistema **não foi liberado para produção pública com dados pessoais**. Antes disso, faltam revisões de segurança, LGPD, migrações de banco e testes E2E completos (ver [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13)).

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

Não inclua senhas, tokens ou outras credenciais no repositório. Use variáveis de ambiente e mantenha os dados de acesso privados.

## ✅ Executar uma cópia baixada do GitHub (Windows)

Este projeto já foi testado localmente com **Docker, PostgreSQL 16, Java 17 e Maven**. A aplicação original funcionou na porta `8080`; uma cópia obtida do GitHub pôde ser iniciada separadamente na porta `8081`, conectada a um banco PostgreSQL de teste na porta `55432`.

**Atenção: o download do repositório não inclui os dados armazenados no PostgreSQL.** Ao usar `mvn spring-boot:run` sem configurar `DB_URL`, a aplicação inicia por padrão com H2 em memória, que não contém os animais, ONGs ou usuários do ambiente original. Isso não significa que os registros foram apagados. As imagens estáticas versionadas também precisam estar presentes na pasta `src/main/resources/static/`; dados do banco e arquivos de imagem são recursos distintos.

### Opção A — Instalação independente (recomendada para novos avaliadores)

1. Faça checkout da branch `main`, que contém a versão integrada.
2. Na raiz, copie `.env.example` para `.env` e preencha `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `ADOTAPET_ADMIN_EMAIL` e `ADOTAPET_ADMIN_PASSWORD` com **valores novos e privados**. Veja as exigências e como gerar o segredo JWT em [Homologação local](docs/HOMOLOGACAO_LOCAL.md).
3. Com o Docker Desktop aberto, execute `docker compose up --build -d` e consulte `docker compose logs -f aplicacao`.
4. Abra `http://localhost:8080` e acesse `http://localhost:8080/painel.html` para cadastrar os abrigos e os animais de teste. Se as portas já estiverem ocupadas, não inicie outra cópia do Compose sem ajustar o mapeamento.
5. Execute `mvn test` para verificar os testes automatizados Java, se tiver o JDK e o Maven instalados.

O provisionamento administrativo é **explícito**: quando as variáveis de administrador estão configuradas, a aplicação cria a conta **somente se o e-mail ainda não existir**. Não há senha administrativa universal no repositório. Alterar a variável de senha não redefine automaticamente a senha de uma conta existente.

### Dados demonstrativos opcionais (somente desenvolvimento)

O código já possui a classe `DemoDataBootstrap`, que pode cadastrar **4 abrigos fictícios e 16 animais fictícios**, além de exemplos de vacinação e solicitações. A carga é **desativada por padrão**; só é habilitada quando o perfil `dev` está ativo **e** a propriedade `adotapet.demo.seed=true` é informada. Para experimentar sem os dados pessoais do banco original, execute em uma instância H2 separada:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081 --spring.profiles.active=dev --adotapet.demo.seed=true"
```

Não habilite essa carga em produção nem em bancos que contenham registros reais. Os tutores de demonstração recebem senhas aleatórias, **não** uma senha pública de acesso. A carga demonstrativa também **não cria uma conta ADMIN**: para configurar uma conta administrativa, utilize as variáveis `ADOTAPET_ADMIN_EMAIL` e `ADOTAPET_ADMIN_PASSWORD` conforme o guia de homologação. A presença dos registros fictícios não garante fotografias individuais, pois dados de animais e arquivos de mídia são recursos diferentes.

### Opção B — Conferir uma cópia contra um backup local (homologação)

**Somente para quem já possui um backup autorizado e privado do banco.** Não publique dumps de banco, arquivos `.env`, dados pessoais ou credenciais no GitHub. O exemplo abaixo presume um contêiner *separado* chamado `adotapet-banco-teste`, com PostgreSQL já inicializado, e o backup local em `Downloads\adotapet-backup.dump`.

No PowerShell, confira o serviço e copie o arquivo para o contêiner de teste:

~~~powershell
docker exec adotapet-banco-teste pg_isready -U Adotapet -d adotapet
docker cp "$env:USERPROFILE\Downloads\adotapet-backup.dump" adotapet-banco-teste:/tmp/adotapet-backup.dump
~~~

**Restaurar apenas num banco vazio e separado**, nunca sobre o banco original ou um banco com dados a preservar:

~~~powershell
docker exec adotapet-banco-teste pg_restore -U Adotapet -d adotapet --no-owner --no-acl --exit-on-error /tmp/adotapet-backup.dump
~~~

Para executar o código da cópia baixada com esse PostgreSQL, abra um **novo terminal PowerShell**, entre na pasta que contém `pom.xml` e configure as variáveis abaixo. O valor da senha deve corresponder à senha *já definida* no PostgreSQL de teste; os comandos não alteram a senha do banco:

~~~powershell
$env:DB_URL = "jdbc:postgresql://localhost:55432/adotapet"
$env:DB_USER = "Adotapet"
$env:DB_PASSWORD = Read-Host "Senha do PostgreSQL de teste"
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
~~~

Depois, visite `http://localhost:8081` e confira o catálogo, as ONGs, as imagens e o login no painel. **Os registros da conta administrativa podem ser restaurados com o backup**; as credenciais devem ser mantidas privadas. O exemplo usa portas e nome de banco específicos de um cenário de testes, não configurações obrigatórias para qualquer instalação.

> **Não execute `docker compose down -v` se quiser manter os dados:** o sinalizador `-v` remove volumes. Nunca restaure dados de produção em uma instalação pública de demonstração.

## 🖥️ Interface web e integração

- **Demonstração pública gratuita (somente frontend):** https://eloisafazzio.github.io/Projeto_AdotaPet/
- **Site público:** http://localhost:8080 (animais disponíveis, filtros, login e solicitações).
- **Painel da ONG:** http://localhost:8080/painel.html (gestão de animais, abrigos, vacinação, pedidos, visitas e relatórios).
- **Swagger/OpenAPI:** http://localhost:8080/swagger-ui.html (somente em desenvolvimento).
- A integração dos PRs #9, #14, #17, #10 e #15 foi incorporada à `main` pelo [PR #20](https://github.com/EloisaFazzio/Projeto_AdotaPet/pull/20).
- O catálogo **não é pré-preenchido na instalação padrão**. Para visualizar animais, cadastre dados pelo painel ou, exclusivamente no ambiente de desenvolvimento, habilite a carga fictícia opcional descrita acima.
- Há testes automatizados Java, HTTP e de navegador. O funcionamento do ambiente original em `8080` foi confirmado em teste local; uma restauração de backup em banco isolado retornou **16 animais**. A validação visual comparativa completa, o login por perfil e a publicação em produção **não foram comprovados por essa restauração** e seguem pendentes de evidências adicionais.

## 📚 Documentação

A documentação acadêmica da Etapa 1 está organizada na pasta [`docs/`](docs/).

- **[Diagrama de Classe](docs/Diagrama%20de%20Classe.svg)** — visão conceitual das principais responsabilidades do domínio.
- **[Atualização da Modelagem](docs/Atualizacao-da-Modelagem.md)** — explica os agrupamentos do diagrama e sua compatibilidade com o modelo persistente.
- **[Mapeamento de Entidades JPA](docs/Mapeamento%20de%20Entidades%20JPA.md)** — entidades, tabelas, chaves e relacionamentos.
- **[Dicionário de Dados](docs/Dicion%C3%A1rio%20de%20Dados.md)** — campos, tipos, obrigatoriedade e restrições.
- **[Implementação full stack](docs/IMPLEMENTACAO_FULLSTACK.md)** — endpoints e etapas da API.
- **[Painel da ONG](docs/PAINEL_ONG.md)** — operações administrativas e limitações.
- **[Rastreabilidade e Swagger](docs/API_RASTREABILIDADE_E_SWAGGER.md)** — endpoints adicionais da matriz.
- **[Homologação local](docs/HOMOLOGACAO_LOCAL.md)** — como iniciar o site completo com Docker Compose no Windows.
- **[Auditoria de arquivos e entrega](docs/AUDITORIA_FINAL.md)** — classificação de arquivos ativos, redundantes e possíveis resíduos. O PDF acadêmico da Etapa 1 foi entregue separadamente e ainda não está versionado neste repositório.

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

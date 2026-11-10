# AdotaPet — Guia de homologação local (Windows)

O site público, o backend e o painel administrativo estão combinados na branch **feat/integracao-homologacao-adotapet** (PR #20). A branch **main** continua inalterada até a aprovação da integração.

Este ambiente é apenas para **testes acadêmicos com dados fictícios**. Não use dados pessoais reais, fotos privadas ou senhas reutilizadas.

## 1. Requisitos

- Git
- Docker Desktop no Windows (com o comando docker compose)
- Navegador atualizado

A configuração local usa **Java 17**, Spring Boot e **PostgreSQL 16** em contêineres.

## 2. Baixar e preparar o projeto

Abra o PowerShell:

~~~powershell
git clone https://github.com/EloisaFazzio/Projeto_AdotaPet.git
cd Projeto_AdotaPet
git checkout feat/integracao-homologacao-adotapet
Copy-Item .env.example .env
notepad .env
~~~

Se você já possui o repositório local, faça git fetch e selecione a branch de integração, em vez de executar git clone novamente.

No arquivo **.env**, substitua os campos vazios:

- **DB_USER**: usuário local do PostgreSQL (pode permanecer adotapet).
- **DB_PASSWORD**: senha forte para o banco de dados.
- **ADOTAPET_ADMIN_EMAIL**: e-mail da conta administradora inicial.
- **ADOTAPET_ADMIN_PASSWORD**: senha com pelo menos 12 caracteres, diferente da senha do banco.
- **ADOTAPET_ADMIN_NAME**: nome da pessoa administradora.
- **JWT_SECRET**: pelo menos 32 bytes aleatórios codificados em Base64.

Para gerar o JWT_SECRET sem sites de terceiros, execute no PowerShell:

~~~powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
~~~

Copie o resultado para o campo JWT_SECRET do arquivo .env. O arquivo .env está incluído no .gitignore para não ser enviado ao GitHub.

## 3. Iniciar o site

~~~powershell
docker compose up --build -d
docker compose logs -f aplicacao
~~~

Quando o servidor terminar de inicializar, acesse no navegador:

| Endereço | Função |
|---|---|
| http://localhost:8080 | Site público do AdotaPet |
| http://localhost:8080/painel.html | Painel da ONG |
| http://localhost:8080/swagger-ui.html | Documentação interativa da API (somente desenvolvimento) |

Faça login com as credenciais administrativas definidas no .env. Em seguida, clique em **Área da ONG** para abrir o painel.

## 4. Sequência sugerida de testes

1. **Abrigos:** com ADMIN, cadastre um abrigo fictício com sua capacidade.
2. **Equipe:** crie um usuário VOLUNTARIO e vincule-o ao abrigo.
3. **Animais:** cadastre um animal fictício. Ele deve aparecer no catálogo público enquanto DISPONIVEL.
4. **Vacinação:** crie uma vacina compatível com a espécie e registre sua aplicação.
5. **Adotante:** crie uma conta distinta usando o formulário do site e envie uma solicitação.
6. **Solicitações:** com a equipe, inicie a análise e registre um parecer de aprovação.
7. **Adoção:** formalize com confirmação de que o termo foi assinado; o animal deve passar a ADOTADO.
8. **Visitas:** como VOLUNTARIO, registre a visita de acompanhamento.
9. **Relatórios:** com ADMIN, consulte contagem de animais por status e adoções por período.

Os estados do modelo UML são: DISPONIVEL, EM_TRATAMENTO, EM_PROCESSO_ADOCAO, ADOTADO para Animal; PENDENTE, EM_ANALISE, APROVADA, REJEITADA, CANCELADA para SolicitacaoAdocao.

## 5. Parar o ambiente sem perder os dados

~~~powershell
docker compose down
~~~

O Docker Compose mantém os cadastros no volume **dados_postgres**. **Atenção:** o comando docker compose down -v apaga esse volume e seus registros.

## 6. Alternativa sem Docker

Para desenvolvimento com Java 17 e Maven, configure uma conta administrativa local por variáveis de ambiente e execute:

~~~powershell
$env:ADOTAPET_ADMIN_EMAIL = "admin@exemplo.invalid"
$env:ADOTAPET_ADMIN_PASSWORD = "substitua-por-senha-forte"
mvn spring-boot:run
~~~

O exemplo de e-mail e a senha acima devem ser substituídos por valores exclusivos de teste. Por padrão, a aplicação usa **H2 em memória**, apagado quando o servidor reinicia. Para usar um PostgreSQL existente, configure DB_URL, DB_USER e DB_PASSWORD.

## 7. Testes automáticos

Na branch de integração, execute:

~~~powershell
mvn test
python scripts/smoke_fullstack.py
~~~

O script smoke_fullstack.py exige um servidor já iniciado em http://localhost:8080. As operações administrativas do script são executadas somente se as variáveis ADOTAPET_ADMIN_EMAIL e ADOTAPET_ADMIN_PASSWORD também estiverem disponíveis no terminal.

No GitHub Actions, a pipeline de homologação realiza os testes Maven e JavaScript e sobe o servidor para executar o teste HTTP com credenciais efêmeras geradas pela própria pipeline.

## 8. Limitações importantes

Este ambiente **não é um deploy de produção**. Ainda faltam revisão LGPD, HTTPS na internet, migrações versionadas para PostgreSQL (o projeto usa ddl-auto=update no momento), limites de tentativas de login, testes em navegador com vários dispositivos, armazenamento seguro de fotos reais e avaliações de desempenho. Essas tarefas estão nas Issues #13 e #18. Não exponha a porta 8080 em uma rede pública sem essas medidas.

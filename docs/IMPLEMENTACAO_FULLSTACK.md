# AdotaPet — desenvolvimento full stack (incremental)

Este incremento parte da documentação acadêmica da Etapa 1 e do diagrama UML de oito entidades. As funcionalidades **já codificadas neste PR** são diferentes das funcionalidades **planejadas** nas Issues.

## Tecnologias
- Backend Java 17, Spring Boot 3.3.5, Spring Data JPA, H2 local, PostgreSQL/Supabase.
- Spring Security stateless, BCrypt para senhas, JWT HS256 (8 horas).
- Frontend independente em HTML/CSS/JavaScript, servido por Spring Boot após merge do PR da interface, consumindo a API no mesmo domínio.

## Executar localmente
1. Instalar Java 17 e Maven.
2. Definir opcionalmente `ADOTAPET_ADMIN_EMAIL`, `ADOTAPET_ADMIN_PASSWORD` (12+ caracteres) e `ADOTAPET_ADMIN_NAME` para criar a conta ADMIN inicial. **Não usar uma senha pública ou versioná-la.**
3. Para tokens persistirem entre reinícios, definir `JWT_SECRET` com **32 bytes aleatórios em Base64**, por exemplo: `openssl rand -base64 32`. Sem essa variável, em desenvolvimento é criada uma chave temporária por inicialização. Com perfil `prod`, a variável é obrigatória.
4. Rodar `mvn spring-boot:run`; acessar `http://localhost:8080`.
5. Para PostgreSQL, definir `DB_URL`, `DB_USER`, `DB_PASSWORD`; HTTPS deve ficar ativo no deploy, com validação adicional de infraestrutura.

## Endpoints desta etapa
| Método | Caminho | Perfil | Descrição |
|---|---|---|---|
| POST | /api/auth/registrar | Público | Autocadastro **ADOTANTE** |
| POST | /api/auth/login | Público | Retorna JWT + perfil |
| GET | /api/auth/me | Autenticado | Perfil do usuário |
| GET | /api/abrigos | Público | Lista resumos de abrigos |
| POST | /api/abrigos | ADMIN | Cadastra abrigo |
| GET | /api/animais | Público | Lista paginada; filtros `especie`, `porte`, `sexo`, `cidade`; somente disponíveis para o público |
| GET | /api/animais/{id} | Público | Detalhe de animal disponível |
| POST | /api/animais | ADMIN/VOLUNTARIO | Cadastra animal com validação de capacidade |
| PUT | /api/animais/{id} | ADMIN/VOLUNTARIO | Atualiza cadastro sem alterar status da adoção |
| POST | /api/solicitacoes | ADOTANTE | Solicita adoção de animal disponível |
| GET | /api/usuarios/me/solicitacoes | ADOTANTE | Consulta próprios pedidos |
| GET | /api/solicitacoes | ADMIN/VOLUNTARIO | Lista pedidos, filtro `status` |
| PATCH | /api/solicitacoes/{id}/cancelar | ADOTANTE dono | Cancela enquanto pendente |
| PATCH | /api/solicitacoes/{id}/analisar | ADMIN/VOLUNTARIO | Inicia análise |
| PATCH | /api/solicitacoes/{id}/aprovar | ADMIN/VOLUNTARIO | Aprova com parecer, rejeita ativos concorrentes |
| PATCH | /api/solicitacoes/{id}/rejeitar | ADMIN/VOLUNTARIO | Rejeita com parecer |
| POST | /api/adocoes | ADMIN/VOLUNTARIO | Formaliza após aprovação e termo assinado |
| GET | /api/adocoes/{id} | ADMIN/VOLUNTARIO | Detalhe da adoção |
| POST | /api/adocoes/{id}/visitas | VOLUNTARIO | Registra acompanhamento |
| GET | /api/adocoes/{id}/visitas | ADMIN/VOLUNTARIO | Lista visitas |
| GET | /api/vacinas | ADMIN/VOLUNTARIO | Consulta catálogo |
| POST | /api/vacinas | ADMIN | Cadastra vacina |
| GET | /api/vacinas/{id} | ADMIN/VOLUNTARIO | Consulta vacina |
| PUT | /api/vacinas/{id} | ADMIN | Atualiza vacina |
| DELETE | /api/vacinas/{id} | ADMIN | Exclui vacina sem aplicações |
| POST | /api/animais/{id}/vacinacoes | ADMIN/VOLUNTARIO | Registra dose e valida próxima dose |
| GET | /api/animais/{id}/vacinacoes | ADMIN/VOLUNTARIO | Histórico de vacinação |
| GET | /api/vacinacoes/vencidas | ADMIN/VOLUNTARIO | Doses vencidas |
| GET | /api/relatorios/animais-por-status | ADMIN | Totais por estado |
| GET | /api/relatorios/adocoes-por-periodo?de=&ate= | ADMIN | Adoções em intervalo |


### Exemplos
`POST /api/auth/registrar`: `{"nome":"Pessoa Exemplo","email":"pessoa@exemplo.com","senha":"UmaSenhaForte123!"}`

`POST /api/solicitacoes`: `{"animalId":1,"justificativa":"Tenho condições de oferecer um lar seguro e acompanhamento veterinário."}` com cabeçalho `Authorization: Bearer <token>`.

### Cuidados
- Não existe catálogo fictício pré-carregado: animal e abrigo devem ser cadastrados por um usuário autorizado.
- Nunca inclua foto de banco de imagens como se fosse foto real de um animal cadastrado. O modelo acadêmico ainda não possui mídia/imagens.
- O login usa token no cabeçalho; em produção, considerar renovação e revogação, políticas de senha, limite de tentativas e monitoramento.
- As Issues abertas registram funcionalidades ainda pendentes: gestão completa de usuários/abrigos, interface administrativa de vacinação e relatórios, OpenAPI, desempenho, LGPD e testes mais extensos.
- A camada de persistência `ddl-auto=update` é adequada apenas para desenvolvimento; em produção, planejar migrações versionadas (Flyway ou Liquibase).

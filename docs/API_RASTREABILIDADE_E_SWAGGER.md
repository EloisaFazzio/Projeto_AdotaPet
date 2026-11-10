# API AdotaPet — complemento da matriz de rastreabilidade

Este incremento complementa a API do PR #9 com endpoints que constam na matriz RF x endpoint x entidade (seção 2.3 do trabalho acadêmico), respeitando o modelo UML de oito entidades e as regras de segurança.

## Endpoints adicionados

| Método | Caminho | Permissão | Observação |
|---|---|---|---|
| DELETE | /api/animais/{id} | ADMIN/VOLUNTARIO | Exclui só animais sem solicitações, vacinações ou status de adoção iniciado/concluído. Mantém histórico. |
| GET | /api/solicitacoes/{id} | ADOTANTE titular ou equipe | Titularidade validada no backend. |
| GET | /api/usuarios/{id}/solicitacoes | ADOTANTE titular ou equipe | Evita exposição de solicitações de terceiros. |
| GET | /api/adocoes?de=&ate= | ADMIN/VOLUNTARIO | Parâmetros opcionais, mas ambos devem estar presentes ou ambos ausentes. O período deve ser válido. |

O cadastro público de adotantes permanece em \`POST /api/auth/registrar\` para evitar escalonamento de privilégios.

## OpenAPI / Swagger

- Dependência: \`springdoc-openapi-starter-webmvc-ui:2.6.0\` para Spring Boot 3.3.x.
- Em desenvolvimento: \`http://localhost:8080/swagger-ui.html\`.
- Esquema JSON: \`http://localhost:8080/v3/api-docs\`.
- No Swagger, use **Authorize** com o token JWT obtido no login para operações protegidas.
- O acesso anônimo ao navegador Swagger é habilitado apenas quando o perfil \`prod\` **não** estiver ativo. As rotas reais da API continuam protegidas por Spring Security.
- Em produção, proteja também o acesso à documentação de acordo com a política da organização.

## Testes automatizados

\`RastreabilidadeApiTest\` verifica:
1. geração da especificação OpenAPI sem login em desenvolvimento;
2. exclusão de animal sem histórico somente com perfil autorizado;
3. bloqueio da exclusão quando há vacinação;
4. consulta de solicitações apenas pelo titular ou equipe;
5. validação das datas para relatório de adoções.

## Próximos cuidados

- Implementar paginação de consultas de adoções e solicitações para grandes volumes.
- Refinar auditoria, retenção de registros e LGPD.
- Avaliar migrações de banco, rate limit do login e testes de desempenho antes da implantação.

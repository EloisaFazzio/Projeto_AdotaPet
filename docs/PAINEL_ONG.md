# Painel operacional AdotaPet — Etapa 2

## Dependências e ordem de integração

Esta interface está em um Pull Request empilhado e depende de outros incrementos. Para não quebrar o funcionamento, revise e integre nesta ordem:

1. PR #9 — API inicial, autenticação JWT, cadastro de animais, solicitações, adoção e vacinação;
2. PR #14 — gestão de usuários, abrigos e vínculo de voluntários;
3. PR #17 — completar endpoints REST e Swagger/OpenAPI;
4. PR #10 — site público integrado à API;
5. PR #15 — painel da ONG.

Após a integração de cada PR pai, atualize a base dos PRs dependentes para `main` e aguarde a nova execução da CI. **Não integre o protótipo antigo do PR #1 em paralelo sem resolver os conflitos.**

## Executar o projeto

Pré-requisitos: Java 17 e Maven. Configure uma conta inicial em variáveis de ambiente **sem publicar senhas**, por exemplo no terminal PowerShell (substitua pelos seus valores privados):

```powershell
$env:ADOTAPET_ADMIN_EMAIL = "admin@exemplo.com"
$env:ADOTAPET_ADMIN_PASSWORD = "<defina-uma-senha-forte-com-12-ou-mais-caracteres>"
$env:ADOTAPET_ADMIN_NAME = "Administradora"
mvn spring-boot:run
```

Esses valores são **exemplos**, não credenciais reais. Em produção também defina `JWT_SECRET` (Base64 de 32 ou mais bytes) e use HTTPS + banco PostgreSQL com migrações versionadas.

Acesse `http://localhost:8080`, faça login como ADMIN e clique em **Área da ONG** para abrir `/painel.html`.

## Fluxo de uso recomendado

1. **Abrigos (ADMIN):** cadastre um abrigo e sua capacidade. Editar a capacidade para baixo da ocupação é bloqueado.
2. **Usuários (ADMIN):** crie usuários VOLUNTARIO e vincule-os a um abrigo. Somente voluntários ativos podem ser vinculados.
3. **Animais (ADMIN/VOLUNTARIO):** cadastre animais, preenchendo espécie, porte, sexo e abrigo. O catálogo público mostra apenas DISPONIVEL.
4. **Vacinas (ADMIN):** cadastre o catálogo com a espécie-alvo e um intervalo de doses opcional.
5. **Vacinações (equipe):** registre uma dose em um animal, consulte o histórico e acompanhe as vencidas.
6. **Site público (ADOTANTE):** faça autocadastro, consulte um animal DISPONIVEL e envie justificativa para adoção.
7. **Solicitações (equipe):** inicie análise, aprove ou rejeite com parecer. A aprovação rejeita outros pedidos ativos para o mesmo animal.
8. **Adoções (equipe):** formalize apenas solicitações APROVADAS com termo assinado; depois consulte a adoção e registre visitas (voluntários).
9. **Relatórios (ADMIN):** consulte animais por status, vacinações vencidas e adoções formalizadas por período.

## Proteções e limitações conhecidas

- Apenas o **backend** efetivamente autoriza os acessos; ocultar uma aba no frontend não substitui o controle de permissões.
- O frontend usa `sessionStorage` para a sessão da aba; uma estratégia mais segura de armazenamento/renovação de tokens deve ser avaliada antes da produção (Issue #13).
- Fotos da página pública são apenas ilustrações; o modelo JPA acadêmico ainda não persiste mídia de animais.
- O painel exibe até **36 animais por status**; criar consulta paginada própria para grandes volumes.
- A listagem de solicitações e adoções ainda não é paginada.
- Nem todos os requisitos não funcionais foram demonstrados: HTTPS em implantação, testes de desempenho RNF07 e LGPD precisam ser avaliados antes de produção.
- Antes da entrega final, execute testes integrados no navegador, pois os testes atuais de frontend cobrem sintaxe e estrutura, não fluxo visual ponta a ponta.

## Endpoints de apoio

Em desenvolvimento, a documentação OpenAPI está acessível em `/swagger-ui.html` após integração do PR #17. Use o token obtido em `POST /api/auth/login` para testar rotas protegidas.

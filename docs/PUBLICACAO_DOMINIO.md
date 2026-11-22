# Preparação para publicação pública — AdotaPet

> **Estado:** planejamento técnico. Nenhum domínio foi comprado e nenhuma instância foi publicada por esta alteração.

## Objetivo

Disponibilizar uma **demonstração acadêmica segura** do AdotaPet por HTTPS, preferencialmente em `https://adotapet.com.br`, **somente se o domínio estiver disponível para registro e for registrado pela equipe**. O endereço informado é uma preferência, não um domínio já pertencente ao projeto.

## Arquitetura sugerida

- **Código:** GitHub, branch `main` após PR de homologação aprovada.
- **Aplicação:** serviço Docker/Spring Boot 3.3.5, Java 17, com URL pública HTTPS fornecida por Render ou Railway.
- **Banco:** PostgreSQL persistente da própria hospedagem; nunca conectar o PostgreSQL local ou enviar o backup pessoal.
- **Dados:** apenas registros de demonstração fictícios e autorizados; não colocar dados pessoais, credenciais ou imagens privadas na hospedagem.
- **Domínio:** registro no [Registro.br](https://registro.br/) e configuração de DNS somente após verificação de titularidade e disponibilização do endereço real pelo provedor.

## Ajuste técnico incluído nesta PR

`server.port=${PORT:8080}` em `application.properties` permite respeitar a porta definida pelo provedor, sem mudar a porta local padrão. O Dockerfile existente constrói um JAR com Maven e Java 17. Não existe requisito de renomear `index.html`: o endereço público HTTPS aponta ao serviço, que entrega a página inicial.

## Etapas para implantar

1. Verificar no **Registro.br** se `adotapet.com.br` está disponível e confirmar titularidade/possíveis direitos de marca. Registrar o domínio somente com autorização do titular e aprovação do custo. **Não presumir disponibilidade.**
2. Conectar uma conta de hospedagem (Railway ou Render) ao GitHub e decidir plano, custos e política de cobrança antes de provisionar.
3. Executar a revisão de segurança da [Issue #13](https://github.com/EloisaFazzio/Projeto_AdotaPet/issues/13), inclusive proteção do painel administrativo, autenticação, HTTPS, controle de acesso, LGPD e migrações. A configuração atual `spring.jpa.hibernate.ddl-auto=update` é adequada para homologação local, não para produção.
4. Criar um PostgreSQL **novo e isolado** para a demonstração, com credenciais privadas e política de backup. Não importar `adotapet-backup.dump` nem copiar dados pessoais.
5. Criar o serviço Web a partir deste repositório, usando o Dockerfile, e configurar variáveis no gerenciador de segredos da hospedagem:
   - `SPRING_PROFILES_ACTIVE=prod` (exige JWT_SECRET configurado; revisar segurança primeiro)
   - `DB_URL` no formato `jdbc:postgresql://<host-interno>:5432/<banco>`
   - `DB_USER` e `DB_PASSWORD`
   - `JWT_SECRET` Base64 com **no mínimo 32 bytes aleatórios**
   - `ADOTAPET_ADMIN_EMAIL` e `ADOTAPET_ADMIN_PASSWORD` somente quando necessário para criar conta inicial controlada. Recomenda-se não disponibilizar a administração ao público em demonstrações.
6. Conferir a URL provisória fornecida pela hospedagem e testar site público, imagens, respostas HTTP, logs, autenticação, configurações de privacidade e persistência.
7. Depois de registrar o domínio, adicionar `adotapet.com.br` à hospedagem e criar **somente os registros DNS informados pelo provedor**, validar HTTPS e redirecionamento de HTTP para HTTPS.
8. Adicionar evidências e URL efetivamente publicada na PR/Issue de implantação. Atualizar README apenas quando o endereço funcionar de verdade.

## Cuidados

- Nunca publicar arquivos `.env`, backup PostgreSQL, dados pessoais ou senha da administração no GitHub.
- Imagens existentes em `src/main/resources/static` só devem ser públicas quando houver direito de uso.
- O bootstrap de dados fictícios `DemoDataBootstrap` exige o perfil `dev` e a propriedade `adotapet.demo.seed=true`; **não ativar `dev` em publicação pública**. Uma carga inicial demonstrativa segura em `prod` ainda precisa de implementação e testes.
- O GitHub Pages sozinho **não executa** o backend Spring Boot nem o PostgreSQL.
- Não habilitar deploy público por iniciativa automática antes de concluir o checklist de segurança.

## Critérios de aceite

- [ ] Titular do domínio confirmou aquisição, disponibilidade e direito de uso
- [ ] Hospedagem conectada e custo aprovado
- [ ] Revisão de segurança concluída (Issue #13)
- [ ] CI verde e testes em staging aprovados
- [ ] Banco de demonstração sem dados pessoais
- [ ] URL de hospedagem acessível por HTTPS
- [ ] Domínio registrado e DNS apontado para serviço
- [ ] Página, imagens, formulários e painel verificados conforme exposição permitida
- [ ] Procedimento de rollback e monitoramento documentados

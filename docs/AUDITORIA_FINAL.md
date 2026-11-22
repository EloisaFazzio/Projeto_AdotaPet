# Auditoria final do repositório AdotaPet — 2026

## Objetivo e limites

Revisão da árvore de arquivos da branch `main`, dos HTML/CSS/JavaScript públicos, da estrutura Spring Boot, dos testes, documentação, workflows e estado de Issues/PRs. A análise de uso de arquivos estáticos considera referências em `index.html`, `painel.html`, `conteudo.html`, estilos e scripts da interface. Um nome sem referência literal pode ser construído dinamicamente; por isso, somente resíduos com ausência de referência e sem papel identificado são removidos. Esta auditoria **não** equivale a uma análise completa de cobertura de código, licença de imagens ou teste visual em todos os navegadores.

## Organização válida (não remover)

| Caminho | Uso |
| --- | --- |
| `src/main/java/` | Backend Spring Boot, modelos, JPA, controladores, regras e segurança |
| `src/main/resources/static/` | Interface original entregue pelo backend Java |
| `src/test/` | Testes Java |
| `tests/e2e/`, `playwright.config.mjs`, `package.json` | Testes automatizados de navegador |
| `site-demo/` | Cópia adaptada do frontend original para o GitHub Pages gratuito |
| `site-demo/demo-api.js` | Simulação local de API com dados fictícios e sem gravação |
| `docs/` | Diagramas, JPA, dicionário, documentação e homologação |
| `.github/workflows/` | Testes Java, frontend, integração e publicação gratuita |
| `Dockerfile`, `compose.yaml`, `.env.example` | Execução local/ambiente Docker; `.env` não é versionado |

**A duplicação entre `site-demo` e `src/main/resources/static` é intencional e necessária na configuração atual:** a primeira pasta publica somente o frontend no GitHub Pages, e a segunda é distribuída junto ao backend Java. Não excluir uma delas só por terem nomes repetidos.

## Arquivos removidos nesta limpeza (18 entradas, 9 por pasta)

Os arquivos abaixo não tinham referência de carregamento no HTML/CSS/JS inspecionado; mantêm-se recuperáveis no histórico Git. A limpeza foi aplicada **em ambas as cópias** para evitar resíduos.

- `galeria-final.js`: script que não é carregado por nenhum HTML ativo.
- `icons.svg`: conjunto SVG externo não referenciado; a interface utiliza ícones inline.
- `imagens-pets/banguela-girassol.webp`: variação sem referência.
- `imagens-pets/batmiau-heroi.webp`: variação sem referência.
- `imagens-pets/calopsita-fashion.webp`: variação sem referência.
- `imagens-pets/dona-confusao.webp`: variação sem referência.
- `imagens-pets/nazare-surpresa.webp`: variação sem referência.
- `imagens-pets/safadinha-borboleta.webp`: variação sem referência.
- `imagens-pets/zeca-flor.webp`: variação sem referência.

Os arquivos de fotos utilizados pela aplicação (por exemplo `batmiau.webp`, `banguela.webp`, `sol.webp`, `pinga.webp`) **foram preservados**. Imagens com nomes semelhantes não devem ser confundidas.

## Itens examinados e preservados por prudência

- `conteudo.html` e `conteudo.js`: a página não possui link direto no menu inicial, mas pode ser acessada diretamente e o script é usado por essa página. **Não é seguro classificá-los como arquivos fantasmas sem confirmar se a gestão de conteúdo ainda será utilizada.**
- `imagens-ongs/*.svg`: ilustrações/logotipos alternativos sem referência direta na interface atual. Mantidos por poderem integrar a documentação e o material gráfico do projeto; decidir separadamente sobre descarte definitivo.
- `fotos-demonstracao.js`, `fotos-pets-final.js`, `painel-final.js` e folhas CSS de ajustes: há referências nas páginas principais; apesar dos nomes de revisão, **não são arquivos fantasmas**.
- PRs e branches antigas, testes, documentação acadêmica e arquivos de configuração: fazem parte do histórico ou da execução local e não devem ser excluídos automaticamente.

## Verificações e condições da entrega

- A integração da PR #20 foi incorporada à `main`.
- A demonstração do repositório é publicada em `https://eloisafazzio.github.io/Projeto_AdotaPet/` (GitHub Pages). O endereço antigo do Cloudflare Pages foi retirado da indicação pública após relato de conteúdo divergente. **É estática**: não oferece autenticação ou adoções reais.
- O sistema Java completo deve continuar sendo testado localmente pelo guia `docs/HOMOLOGACAO_LOCAL.md`.
- Após a limpeza, validar CI do backend, frontend e GitHub Pages e inspecionar visualmente o site desktop e celular.
- A publicação de dados reais ainda depende de segurança, LGPD, migrações e testes E2E, conforme Issue #13. Não declarar o backend pronto para produção.
- O domínio `adotapet.com.br` não foi registrado; a hospedagem do site demonstrativo no Cloudflare Pages e o espelho no GitHub Pages permanecem gratuitos.
- A integração do antigo Cloudflare Pages foi configurada fora deste repositório. Confirmar no painel Cloudflare a qual projeto o domínio foi atribuído antes de voltar a divulgá-lo.

## Histórico e manutenção

Não remover arquivos de imagens, migrations ou Java por suspeita apenas nominal. Para qualquer limpeza futura, verificar referências dinâmicas, endpoints, CSS e testes, abrir PR e confirmar execução da CI. Arquivos excluídos permanecem recuperáveis em commits anteriores.

## Complemento da revisão de consistência — 22/11/2026

- Confirmadas oito classes `@Entity` e seis grupos conceituais do `Diagrama de Classe.svg`.
- Corrigida omissão documental dos campos binários opcionais `imagem_dados` e de seus tipos MIME em `Animal` e `Vacina`, sem alterar o código ou remover recursos.
- Conferidos os controladores e testes de rastreabilidade; pendências de produção e de homologação manual permanecem identificadas nas Issues.
- A responsável informou que a apresentação em computador e celular está correta após a substituição das setas Unicode por SVGs (commit `6a52b7f`).
- Capturas de tela e relatórios de evidências não fazem parte do escopo solicitado para esta revisão.

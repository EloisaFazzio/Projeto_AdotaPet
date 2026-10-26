# Atualização da modelagem conceitual — AdotaPet

## Objetivo

O diagrama foi reorganizado para apresentar as responsabilidades principais do sistema de forma mais objetiva, agrupando visualmente conceitos relacionados. O título foi padronizado para **Diagrama de Classe**, sem indicação de versão.

## Grupos apresentados no diagrama

1. **Abrigo** — dados essenciais do abrigo.
2. **Animal** — cadastro e situação do animal.
3. **Vacinação do Animal** — dados da vacina e informações de cada aplicação.
4. **Usuário** — perfis de administrador, voluntário e adotante.
5. **Processo de Adoção** — solicitação e formalização da adoção.
6. **Acompanhamento Pós-Adoção** — visitas e observações sobre o bem-estar do animal.

## Compatibilidade com o modelo JPA

O agrupamento é apenas visual/conceitual. Ele não elimina nem funde as entidades implementadas no backend. O modelo JPA continua documentando oito entidades persistidas:

- `Usuario`
- `Abrigo`
- `Animal`
- `Vacina`
- `VacinacaoAnimal`
- `SolicitacaoAdocao`
- `Adocao`
- `VisitaAcompanhamento`

As classes `Vacina` e `VacinacaoAnimal` permanecem distintas porque o histórico precisa registrar várias aplicações de uma mesma vacina, com datas e informações próprias. Da mesma forma, `SolicitacaoAdocao` e `Adocao` representam etapas diferentes: pedido e formalização.

Consulte também o [Mapeamento de Entidades JPA](Mapeamento%20de%20Entidades%20JPA.md) e o [Dicionário de Dados](Dicion%C3%A1rio%20de%20Dados.md) para obter o detalhamento técnico dos campos e relacionamentos.

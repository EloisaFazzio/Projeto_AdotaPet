# AdotaPet

Plataforma de adoção de animais para ONGs. Projeto Final, Etapa 1: backend, modelagem e requisitos.

Backend em Java 17, Spring Boot 3 e Spring Data JPA.

## Equipe

| Integrante | Função |
|---|---|
| Fabrício da Silva | Líder |
| Eloisa Fazzio da Silva Rocha | Integrante |
| Eric Eduardo Lange | Integrante |

## Documentação

O documento formal da Etapa 1 está em [`docs/`](docs/) (`documentacao-etapa1.docx`), com requisitos, matriz de rastreabilidade, diagrama de classes UML e dicionário de dados.

## Estrutura

```
src/main/java/br/edu/catolicasc/adotapet/
├── AdotaPetApplication.java
├── model/          # 8 entidades JPA com regras de domínio
│   └── enums/      # Perfil, Especie, Sexo, Porte, StatusAnimal, StatusSolicitacao
└── repository/     # 8 interfaces JpaRepository (Spring Data JPA)
```

## Relacionamentos mapeados

| Tipo | Onde |
|---|---|
| 1:N (`@ManyToOne` / `@OneToMany`) | Abrigo→Animal, Usuario→SolicitacaoAdocao, Animal→SolicitacaoAdocao, Adocao→VisitaAcompanhamento, entre outros |
| N:M direto (`@ManyToMany` + `@JoinTable`) | Usuario (voluntário) ↔ Abrigo, tabela `voluntario_abrigo` |
| N:M com atributos (entidade associativa) | Animal ↔ Vacina, por `VacinacaoAnimal` |
| 1:1 (`@OneToOne`) | SolicitacaoAdocao ↔ Adocao |

## Executar

Requisitos: JDK 17 e Maven 3.9+.

```bash
mvn spring-boot:run
```

Por padrão usa H2 em memória (modo PostgreSQL). Para PostgreSQL/Supabase, defina `DB_URL`, `DB_USER` e `DB_PASSWORD`
(exemplo: `DB_URL=jdbc:postgresql://host:5432/postgres`).

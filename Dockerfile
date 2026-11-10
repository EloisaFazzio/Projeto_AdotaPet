# Ambiente acadêmico de homologação. Produção exige revisão de segurança e migrações.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd --create-home --uid 10001 adotapet
COPY --from=build --chown=adotapet:adotapet /build/target/adotapet-*.jar /app/adotapet.jar
USER adotapet
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/adotapet.jar"]

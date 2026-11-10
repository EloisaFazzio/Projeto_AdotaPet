package br.edu.catolicasc.adotapet.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

/** Documentação de rotas conforme RNF09; o botão Authorize aceita token JWT Bearer. */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "AdotaPet — API REST",
        version = "0.2.0",
        description = "Plataforma de adoção responsável para ONGs. As operações administrativas exigem autenticação JWT e perfil autorizado."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP,
        scheme = "bearer", bearerFormat = "JWT", description = "Token retornado em POST /api/auth/login")
public class OpenApiConfig {
}

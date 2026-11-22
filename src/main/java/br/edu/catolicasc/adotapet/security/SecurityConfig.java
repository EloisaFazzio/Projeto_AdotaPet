package br.edu.catolicasc.adotapet.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(
                (request, response, ex) -> response.sendError(401, "Autenticação necessária")))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/painel.html", "/painel.css", "/painel.js", "/favicon.ico", "/imagens-pets/**", "/imagens-memes.css", "/catalogo-corrigido.css", "/imagens-ongs/**", "/imagens-pets/**", "/ajustes-ongs-vacinas.css", "/fotos-ongs/**", "/imagens-pets/**", "/fotos-pets-final.js", "/galeria-final.js", "/painel-final.js", "/fotos-pets-final.css", "/imagens-pets/**", "/error", "/icons.svg", "/etapa1.css", "/conteudo.html", "/conteudo.js", "/conteudo-publico.js", "/ongs.js", "/ajustes.css", "/fotos-demonstracao.js").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registrar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/animais", "/api/animais/**", "/api/abrigos", "/api/conteudo").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

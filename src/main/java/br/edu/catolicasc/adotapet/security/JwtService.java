package br.edu.catolicasc.adotapet.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/** JWT assinado HS256. Em produção, JWT_SECRET (Base64 de 32+ bytes) é obrigatório. */
@Service
public class JwtService {
    private static final Logger LOG = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey chave;
    public JwtService(@Value("$" + "{JWT_SECRET:}") String segredoBase64, Environment env) {
        if (segredoBase64 == null || segredoBase64.isBlank()) {
            for (String profile : env.getActiveProfiles()) {
                if ("prod".equalsIgnoreCase(profile)) throw new IllegalStateException("Configure JWT_SECRET para produção.");
            }
            byte[] temporaria = new byte[32];
            new SecureRandom().nextBytes(temporaria);
            this.chave = Keys.hmacShaKeyFor(temporaria);
            LOG.warn("JWT_SECRET não definido: chave temporária de desenvolvimento; tokens expiram ao reiniciar.");
        } else {
            byte[] bytes = Base64.getDecoder().decode(segredoBase64);
            if (bytes.length < 32) throw new IllegalStateException("JWT_SECRET precisa conter pelo menos 32 bytes em Base64.");
            this.chave = Keys.hmacShaKeyFor(bytes);
        }
    }
    public String gerar(String email) {
        long agora = System.currentTimeMillis();
        return Jwts.builder().subject(email).issuedAt(new Date(agora))
                .expiration(new Date(agora + 8L * 60 * 60 * 1000)).signWith(chave).compact();
    }
    public String email(String token) {
        return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload().getSubject();
    }
}

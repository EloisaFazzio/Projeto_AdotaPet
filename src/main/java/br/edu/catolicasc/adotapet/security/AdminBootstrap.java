package br.edu.catolicasc.adotapet.security;
import br.edu.catolicasc.adotapet.model.Usuario;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
/** Provisionamento explícito, sem contas ou senhas padrão no repositório. */
@Configuration
public class AdminBootstrap {
    @Bean ApplicationRunner adminInicial(UsuarioRepository usuarios, PasswordEncoder encoder,
            @Value("$" + "{ADOTAPET_ADMIN_EMAIL:}") String email,
            @Value("$" + "{ADOTAPET_ADMIN_PASSWORD:}") String senha,
            @Value("$" + "{ADOTAPET_ADMIN_NAME:Administrador}") String nome) {
        return args -> {
            if (email.isBlank() && senha.isBlank()) return;
            if (email.isBlank() || senha.length() < 12)
                throw new IllegalStateException("Informe ADOTAPET_ADMIN_EMAIL e ADOTAPET_ADMIN_PASSWORD de 12+ caracteres.");
            String normalizado = email.trim().toLowerCase(java.util.Locale.ROOT);
            if (!usuarios.existsByEmail(normalizado)) {
                Usuario admin = new Usuario();
                admin.setNome(nome); admin.setEmail(normalizado);
                admin.setSenhaHash(encoder.encode(senha));
                admin.setPerfil(Perfil.ADMIN); admin.setAtivo(true);
                usuarios.save(admin);
            }
        };
    }
}

package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Usuario;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.UsuarioRepository;
import br.edu.catolicasc.adotapet.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios; this.encoder = encoder; this.jwt = jwt;
    }

    public record Cadastro(@NotBlank String nome, @Email @NotBlank String email,
                           @Size(min = 8, max = 72) @NotBlank String senha,
                           String telefone, String cidade) {}
    public record Login(@Email @NotBlank String email, @NotBlank String senha) {}
    public record PerfilUsuario(Long id, String nome, String email, Perfil perfil) {
        public static PerfilUsuario of(Usuario u) { return new PerfilUsuario(u.getId(), u.getNome(), u.getEmail(), u.getPerfil()); }
    }
    public record Sessao(String token, PerfilUsuario usuario) {}

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Sessao registrar(@Valid @RequestBody Cadastro entrada) {
        String email = entrada.email().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        Usuario usuario = new Usuario();
        usuario.setNome(entrada.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(encoder.encode(entrada.senha()));
        usuario.setTelefone(entrada.telefone());
        usuario.setCidade(entrada.cidade());
        usuario.setPerfil(Perfil.ADOTANTE); // perfis elevados nunca são aceitos no autocadastro
        usuario.setAtivo(true);
        usuarios.saveAndFlush(usuario);
        return new Sessao(jwt.gerar(usuario.getEmail()), PerfilUsuario.of(usuario));
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public Sessao login(@Valid @RequestBody Login entrada) {
        Usuario usuario = usuarios.findByEmail(entrada.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));
        if (!usuario.isAtivo() || !encoder.matches(entrada.senha(), usuario.getSenhaHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        return new Sessao(jwt.gerar(usuario.getEmail()), PerfilUsuario.of(usuario));
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public PerfilUsuario me(Authentication autenticacao) {
        Usuario usuario = usuarios.findByEmail(autenticacao.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return PerfilUsuario.of(usuario);
    }
}

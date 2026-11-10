package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Usuario;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** RF02: gestão administrativa de contas; histórico é preservado por desativação lógica. */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder senhas;

    public UsuarioController(UsuarioRepository usuarios, PasswordEncoder senhas) {
        this.usuarios = usuarios;
        this.senhas = senhas;
    }

    public record UsuarioResumo(Long id, String nome, String email, String telefone,
                                String cidade, Perfil perfil, boolean ativo) {
        static UsuarioResumo of(Usuario u) {
            return new UsuarioResumo(u.getId(), u.getNome(), u.getEmail(),
                    u.getTelefone(), u.getCidade(), u.getPerfil(), u.isAtivo());
        }
    }

    public record NovoUsuario(@NotBlank @Size(max = 120) String nome,
                              @NotBlank @Email @Size(max = 160) String email,
                              @NotBlank @Size(min = 8, max = 72) String senha,
                              @Size(max = 20) String telefone,
                              @Size(max = 80) String cidade,
                              @NotNull Perfil perfil) {}

    public record EditarUsuario(@NotBlank @Size(max = 120) String nome,
                                @NotBlank @Email @Size(max = 160) String email,
                                @Size(min = 8, max = 72) String novaSenha,
                                @Size(max = 20) String telefone,
                                @Size(max = 80) String cidade,
                                @NotNull Perfil perfil) {}

    @GetMapping
    @Transactional(readOnly = true)
    public List<UsuarioResumo> listar(@RequestParam(required = false) Perfil perfil) {
        List<Usuario> lista = perfil == null ? usuarios.findAll() : usuarios.findByPerfil(perfil);
        return lista.stream().map(UsuarioResumo::of).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public UsuarioResumo consultar(@PathVariable Long id) {
        return UsuarioResumo.of(buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public UsuarioResumo cadastrar(@Valid @RequestBody NovoUsuario entrada) {
        String email = emailNormalizado(entrada.email());
        if (usuarios.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(entrada.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(senhas.encode(entrada.senha()));
        usuario.setTelefone(entrada.telefone());
        usuario.setCidade(entrada.cidade());
        usuario.setPerfil(entrada.perfil());
        usuario.setAtivo(true);
        return UsuarioResumo.of(usuarios.saveAndFlush(usuario));
    }

    @PutMapping("/{id}")
    @Transactional
    public UsuarioResumo editar(@PathVariable Long id, @Valid @RequestBody EditarUsuario entrada,
                                Authentication autenticacao) {
        Usuario usuario = buscar(id);
        String novoEmail = emailNormalizado(entrada.email());
        if (!usuario.getEmail().equals(novoEmail) && usuarios.existsByEmail(novoEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        if (usuario.getEmail().equals(autenticacao.getName()) && entrada.perfil() != Perfil.ADMIN) {
            throw new IllegalStateException("Não é permitido remover seu próprio perfil de administrador.");
        }
        if (usuario.getPerfil() == Perfil.VOLUNTARIO && entrada.perfil() != Perfil.VOLUNTARIO
                && !usuario.getAbrigos().isEmpty()) {
            throw new IllegalStateException("Desvincule o voluntário de todos os abrigos antes de alterar o perfil.");
        }
        usuario.setNome(entrada.nome().trim());
        usuario.setEmail(novoEmail);
        usuario.setTelefone(entrada.telefone());
        usuario.setCidade(entrada.cidade());
        usuario.setPerfil(entrada.perfil());
        if (entrada.novaSenha() != null && !entrada.novaSenha().isBlank()) {
            usuario.setSenhaHash(senhas.encode(entrada.novaSenha()));
        }
        return UsuarioResumo.of(usuarios.saveAndFlush(usuario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void desativar(@PathVariable Long id, Authentication autenticacao) {
        Usuario usuario = buscar(id);
        if (usuario.getEmail().equals(autenticacao.getName())) {
            throw new IllegalStateException("Não é permitido desativar sua própria conta administrativa.");
        }
        // Invalida novas requisições com tokens antigos: JwtAuthFilter verifica 'ativo' no banco.
        usuario.desativar();
    }

    @PatchMapping("/{id}/reativar")
    @Transactional
    public UsuarioResumo reativar(@PathVariable Long id) {
        Usuario usuario = buscar(id);
        usuario.setAtivo(true);
        return UsuarioResumo.of(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    private static String emailNormalizado(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Abrigo;
import br.edu.catolicasc.adotapet.model.Usuario;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.AbrigoRepository;
import br.edu.catolicasc.adotapet.repository.AnimalRepository;
import br.edu.catolicasc.adotapet.repository.UsuarioRepository;
import br.edu.catolicasc.adotapet.model.enums.StatusAnimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** RF03 e RN07/RN10: gestão de abrigos e vínculo N:M de voluntários. */
@RestController
@RequestMapping("/api/abrigos")
public class AbrigoController {
    private final AbrigoRepository abrigos;
    private final AnimalRepository animais;
    private final UsuarioRepository usuarios;

    public AbrigoController(AbrigoRepository abrigos, AnimalRepository animais, UsuarioRepository usuarios) {
        this.abrigos = abrigos;
        this.animais = animais;
        this.usuarios = usuarios;
    }

    public record AbrigoResumo(Long id, String nome, String cidade, String uf, int capacidade) {
        public static AbrigoResumo of(Abrigo a) {
            return new AbrigoResumo(a.getId(), a.getNome(), a.getCidade(), a.getUf(), a.getCapacidade());
        }
    }

    public record AbrigoDetalhe(Long id, String nome, String endereco, String cidade,
                                String uf, String telefone, int capacidade, long ocupacao) {
        public static AbrigoDetalhe of(Abrigo a, long ocupacao) {
            return new AbrigoDetalhe(a.getId(), a.getNome(), a.getEndereco(),
                    a.getCidade(), a.getUf(), a.getTelefone(), a.getCapacidade(), ocupacao);
        }
    }

    public record AbrigoEntrada(@NotBlank @Size(max = 120) String nome,
                                @NotBlank @Size(max = 200) String endereco,
                                @NotBlank @Size(max = 80) String cidade,
                                @NotBlank @Pattern(regexp = "(?i)[A-Z]{2}") String uf,
                                @Size(max = 20) String telefone,
                                @Min(1) int capacidade) {}
    public record VincularVoluntario(@NotNull Long usuarioId) {}
    public record VoluntarioResumo(Long id, String nome, String email) {
        static VoluntarioResumo of(Usuario u) {
            return new VoluntarioResumo(u.getId(), u.getNome(), u.getEmail());
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<AbrigoResumo> listar() {
        return abrigos.findAll().stream().map(AbrigoResumo::of).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AbrigoDetalhe consultar(@PathVariable Long id) {
        Abrigo abrigo = buscar(id);
        return AbrigoDetalhe.of(abrigo, ocupacao(abrigo.getId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AbrigoResumo criar(@Valid @RequestBody AbrigoEntrada entrada) {
        Abrigo abrigo = new Abrigo();
        preencher(abrigo, entrada);
        return AbrigoResumo.of(abrigos.saveAndFlush(abrigo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AbrigoDetalhe atualizar(@PathVariable Long id, @Valid @RequestBody AbrigoEntrada entrada) {
        Abrigo abrigo = abrigos.bloquearPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        long ocupacaoAtual = ocupacao(abrigo.getId());
        if (entrada.capacidade() < ocupacaoAtual) {
            throw new IllegalStateException("Capacidade não pode ser inferior à quantidade de animais não adotados.");
        }
        preencher(abrigo, entrada);
        abrigos.flush();
        return AbrigoDetalhe.of(abrigo, ocupacaoAtual);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void remover(@PathVariable Long id) {
        Abrigo abrigo = abrigos.bloquearPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        // Impede exclusão mesmo se o animal foi adotado: preserva chaves estrangeiras e histórico.
        if (!abrigo.getAnimais().isEmpty()) {
            throw new IllegalStateException("Abrigo com animais cadastrados não pode ser removido.");
        }
        for (Usuario voluntario : new ArrayList<>(abrigo.getVoluntarios())) {
            abrigo.removerVoluntario(voluntario);
        }
        usuarios.flush(); // lado proprietário da associação N:M é Usuario
        abrigos.delete(abrigo);
        abrigos.flush();
    }

    @GetMapping("/{id}/voluntarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<VoluntarioResumo> listarVoluntarios(@PathVariable Long id) {
        return buscar(id).getVoluntarios().stream().map(VoluntarioResumo::of).toList();
    }

    @PostMapping("/{id}/voluntarios")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<VoluntarioResumo> vincular(@PathVariable Long id,
                                           @Valid @RequestBody VincularVoluntario entrada) {
        Abrigo abrigo = abrigos.bloquearPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        Usuario voluntario = usuarios.findById(entrada.usuarioId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (!voluntario.isAtivo() || voluntario.getPerfil() != Perfil.VOLUNTARIO) {
            throw new IllegalArgumentException("Apenas voluntários ativos podem atuar em abrigos.");
        }
        abrigo.adicionarVoluntario(voluntario);
        usuarios.flush(); // grava tabela voluntario_abrigo
        return abrigo.getVoluntarios().stream().map(VoluntarioResumo::of).toList();
    }

    @DeleteMapping("/{id}/voluntarios/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void desvincular(@PathVariable Long id, @PathVariable Long usuarioId) {
        Abrigo abrigo = abrigos.bloquearPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        Usuario voluntario = usuarios.findById(usuarioId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        abrigo.removerVoluntario(voluntario);
        usuarios.flush();
    }

    private Abrigo buscar(Long id) {
        return abrigos.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
    }

    private long ocupacao(Long abrigoId) {
        return animais.countByAbrigoIdAndStatusNot(abrigoId, StatusAnimal.ADOTADO);
    }

    private void preencher(Abrigo a, AbrigoEntrada e) {
        a.setNome(e.nome().trim());
        a.setEndereco(e.endereco().trim());
        a.setCidade(e.cidade().trim());
        a.setUf(e.uf().trim().toUpperCase(Locale.ROOT));
        a.setTelefone(e.telefone());
        a.setCapacidade(e.capacidade());
    }
}

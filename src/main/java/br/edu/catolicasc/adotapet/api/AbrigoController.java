package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Abrigo;
import br.edu.catolicasc.adotapet.repository.AbrigoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abrigos")
public class AbrigoController {
    private final AbrigoRepository abrigos;

    public AbrigoController(AbrigoRepository abrigos) { this.abrigos = abrigos; }

    public record AbrigoResumo(Long id, String nome, String cidade, String uf, int capacidade) {
        public static AbrigoResumo of(Abrigo a) {
            return new AbrigoResumo(a.getId(), a.getNome(), a.getCidade(), a.getUf(), a.getCapacidade());
        }
    }
    public record AbrigoEntrada(@NotBlank String nome, @NotBlank String endereco,
                                @NotBlank String cidade, @NotBlank String uf,
                                String telefone, @Min(1) int capacidade) {}

    @GetMapping
    @Transactional(readOnly = true)
    public List<AbrigoResumo> listar() {
        return abrigos.findAll().stream().map(AbrigoResumo::of).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AbrigoResumo criar(@Valid @RequestBody AbrigoEntrada entrada) {
        Abrigo abrigo = new Abrigo();
        abrigo.setNome(entrada.nome().trim());
        abrigo.setEndereco(entrada.endereco().trim());
        abrigo.setCidade(entrada.cidade().trim());
        abrigo.setUf(entrada.uf().trim().toUpperCase(java.util.Locale.ROOT));
        abrigo.setTelefone(entrada.telefone());
        abrigo.setCapacidade(entrada.capacidade());
        return AbrigoResumo.of(abrigos.saveAndFlush(abrigo));
    }
}

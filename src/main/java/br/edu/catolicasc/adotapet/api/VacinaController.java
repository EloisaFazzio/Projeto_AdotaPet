package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.Especie;
import br.edu.catolicasc.adotapet.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
public class VacinaController {
    private final VacinaRepository vacinas;
    private final VacinacaoAnimalRepository aplicacoes;
    private final AnimalRepository animais;

    public VacinaController(VacinaRepository vacinas, VacinacaoAnimalRepository aplicacoes,
                            AnimalRepository animais) {
        this.vacinas = vacinas; this.aplicacoes = aplicacoes; this.animais = animais;
    }

    public record DadosVacina(Long id, String nome, Especie especieAlvo, Integer intervaloDias) {
        static DadosVacina of(Vacina v) { return new DadosVacina(v.getId(), v.getNome(), v.getEspecieAlvo(), v.getIntervaloDias()); }
    }
    public record EntradaVacina(@NotBlank String nome, @NotNull Especie especieAlvo,
                                @Min(1) Integer intervaloDias) {}
    public record EntradaAplicacao(@NotNull Long vacinaId, @NotNull LocalDate dataAplicacao,
                                   LocalDate proximaDose, String veterinario) {}
    public record DadosAplicacao(Long id, Long animalId, String animal, Long vacinaId, String vacina,
                                 LocalDate dataAplicacao, LocalDate proximaDose, String veterinario) {
        static DadosAplicacao of(VacinacaoAnimal v) {
            return new DadosAplicacao(v.getId(), v.getAnimal().getId(), v.getAnimal().getNome(),
                    v.getVacina().getId(), v.getVacina().getNome(), v.getDataAplicacao(),
                    v.getProximaDose(), v.getVeterinario());
        }
    }

    @GetMapping("/vacinas")
    @Transactional(readOnly = true)
    public List<DadosVacina> listar() {
        return vacinas.findAll().stream().map(DadosVacina::of).toList();
    }

    @GetMapping("/vacinas/{id}")
    @Transactional(readOnly = true)
    public DadosVacina consultar(@PathVariable Long id) {
        return DadosVacina.of(buscarVacina(id));
    }

    @PostMapping("/vacinas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DadosVacina criar(@Valid @RequestBody EntradaVacina entrada) {
        Vacina v = new Vacina();
        preencher(v, entrada);
        return DadosVacina.of(vacinas.saveAndFlush(v));
    }

    @PutMapping("/vacinas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DadosVacina atualizar(@PathVariable Long id, @Valid @RequestBody EntradaVacina entrada) {
        Vacina v = buscarVacina(id);
        preencher(v, entrada);
        return DadosVacina.of(vacinas.saveAndFlush(v));
    }

    @DeleteMapping("/vacinas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void remover(@PathVariable Long id) {
        Vacina v = buscarVacina(id);
        if (!v.getAplicacoes().isEmpty())
            throw new IllegalStateException("Não é possível remover uma vacina com aplicações registradas.");
        vacinas.delete(v);
    }

    @PostMapping("/animais/{id}/vacinacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public DadosAplicacao registrar(@PathVariable Long id, @Valid @RequestBody EntradaAplicacao entrada) {
        Animal animal = animais.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));
        Vacina vacina = buscarVacina(entrada.vacinaId());
        if (vacina.getEspecieAlvo() != animal.getEspecie())
            throw new IllegalArgumentException("A vacina selecionada não atende à espécie deste animal.");
        LocalDate proxima = entrada.proximaDose() != null ? entrada.proximaDose()
                : vacina.calcularProximaDose(entrada.dataAplicacao());
        VacinacaoAnimal registro = animal.registrarVacinacao(vacina, entrada.dataAplicacao(), proxima);
        registro.setVeterinario(entrada.veterinario());
        return DadosAplicacao.of(aplicacoes.saveAndFlush(registro));
    }

    @GetMapping("/animais/{id}/vacinacoes")
    @Transactional(readOnly = true)
    public List<DadosAplicacao> historico(@PathVariable Long id) {
        if (!animais.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado.");
        return aplicacoes.findByAnimalIdOrderByDataAplicacaoDesc(id).stream()
                .map(DadosAplicacao::of).toList();
    }

    @GetMapping("/vacinacoes/vencidas")
    @Transactional(readOnly = true)
    public List<DadosAplicacao> vencidas() {
        return aplicacoes.findByProximaDoseBefore(LocalDate.now()).stream().map(DadosAplicacao::of).toList();
    }

    private Vacina buscarVacina(Long id) {
        return vacinas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacina não encontrada."));
    }
    private void preencher(Vacina vacina, EntradaVacina e) {
        vacina.setNome(e.nome().trim());
        vacina.setEspecieAlvo(e.especieAlvo());
        vacina.setIntervaloDias(e.intervaloDias());
    }
}

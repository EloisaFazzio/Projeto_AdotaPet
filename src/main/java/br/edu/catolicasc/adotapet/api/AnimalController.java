package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;
import br.edu.catolicasc.adotapet.repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {
    private final AnimalRepository animais;
    private final AbrigoRepository abrigos;

    public AnimalController(AnimalRepository animais, AbrigoRepository abrigos) {
        this.animais = animais; this.abrigos = abrigos;
    }

    public record AnimalResumo(Long id, String nome, Especie especie, String raca, Sexo sexo, Porte porte,
                               LocalDate dataNascimento, String descricao, boolean castrado, StatusAnimal status,
                               Long abrigoId, String abrigo, String cidade) {
        public static AnimalResumo of(Animal a) {
            return new AnimalResumo(a.getId(), a.getNome(), a.getEspecie(), a.getRaca(),
                    a.getSexo(), a.getPorte(), a.getDataNascimento(), a.getDescricao(),
                    a.isCastrado(), a.getStatus(), a.getAbrigo().getId(), a.getAbrigo().getNome(),
                    a.getAbrigo().getCidade());
        }
    }
    public record AnimalEntrada(@NotBlank String nome, @NotNull Especie especie, String raca,
                                @NotNull Sexo sexo, @NotNull Porte porte, LocalDate dataNascimento,
                                String descricao, boolean castrado, @NotNull Long abrigoId) {}

    private boolean equipe(Authentication autenticacao) {
        return autenticacao != null && autenticacao.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VOLUNTARIO"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Page<AnimalResumo> listar(@RequestParam(required = false) Especie especie,
                                    @RequestParam(required = false) Porte porte,
                                    @RequestParam(required = false) Sexo sexo,
                                    @RequestParam(required = false) String cidade,
                                    @RequestParam(required = false) StatusAnimal status,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "9") int size,
                                    Authentication autenticacao) {
        if (page < 0 || size < 1) throw new IllegalArgumentException("Paginação inválida.");
        final StatusAnimal filtroStatus = equipe(autenticacao) && status != null ? status : StatusAnimal.DISPONIVEL;
        Specification<Animal> spec = (raiz, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            filtros.add(cb.equal(raiz.get("status"), filtroStatus));
            if (especie != null) filtros.add(cb.equal(raiz.get("especie"), especie));
            if (porte != null) filtros.add(cb.equal(raiz.get("porte"), porte));
            if (sexo != null) filtros.add(cb.equal(raiz.get("sexo"), sexo));
            if (cidade != null && !cidade.isBlank())
                filtros.add(cb.like(cb.lower(raiz.join("abrigo").get("cidade")), "%" + cidade.trim().toLowerCase(java.util.Locale.ROOT) + "%"));
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        Pageable pagina = PageRequest.of(page, Math.min(size, 36), Sort.by("id").descending());
        return animais.findAll(spec, pagina).map(AnimalResumo::of);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AnimalResumo detalhe(@PathVariable Long id, Authentication autenticacao) {
        Animal animal = animais.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));
        if (!equipe(autenticacao) && !animal.estaDisponivel())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não disponível.");
        return AnimalResumo.of(animal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public AnimalResumo criar(@Valid @RequestBody AnimalEntrada entrada) {
        Abrigo abrigo = abrigos.findById(entrada.abrigoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        conferirVaga(abrigo);
        Animal a = new Animal();
        aplicar(a, entrada, abrigo);
        return AnimalResumo.of(animais.saveAndFlush(a));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public AnimalResumo atualizar(@PathVariable Long id, @Valid @RequestBody AnimalEntrada entrada) {
        Animal a = animais.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));
        Abrigo abrigo = abrigos.findById(entrada.abrigoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Abrigo não encontrado."));
        if (!a.getAbrigo().getId().equals(abrigo.getId()) && a.getStatus() != StatusAnimal.ADOTADO) conferirVaga(abrigo);
        aplicar(a, entrada, abrigo);
        return AnimalResumo.of(animais.saveAndFlush(a));
    }

    private void conferirVaga(Abrigo abrigo) {
        long ocupados = animais.countByAbrigoIdAndStatusNot(abrigo.getId(), StatusAnimal.ADOTADO);
        if (!abrigo.possuiVaga(ocupados)) throw new IllegalStateException("Capacidade do abrigo esgotada.");
    }

    private void aplicar(Animal a, AnimalEntrada e, Abrigo abrigo) {
        a.setNome(e.nome().trim());
        a.setEspecie(e.especie());
        a.setRaca(e.raca());
        a.setSexo(e.sexo());
        a.setPorte(e.porte());
        a.setDataNascimento(e.dataNascimento());
        a.setDescricao(e.descricao());
        a.setCastrado(e.castrado());
        a.setAbrigo(abrigo);
    }
}

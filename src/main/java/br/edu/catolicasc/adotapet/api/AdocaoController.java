package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/adocoes")
@PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
public class AdocaoController {
    private final AdocaoRepository adocoes;
    private final SolicitacaoAdocaoRepository solicitacoes;
    private final AnimalRepository animais;
    private final UsuarioRepository usuarios;
    private final VisitaAcompanhamentoRepository visitas;

    public AdocaoController(AdocaoRepository adocoes, SolicitacaoAdocaoRepository solicitacoes,
                            AnimalRepository animais, UsuarioRepository usuarios,
                            VisitaAcompanhamentoRepository visitas) {
        this.adocoes = adocoes; this.solicitacoes = solicitacoes;
        this.animais = animais; this.usuarios = usuarios; this.visitas = visitas;
    }
    public record Formalizacao(@NotNull Long solicitacaoId, boolean termoAssinado, String observacoes) {}
    public record AdocaoResumo(Long id, Long solicitacaoId, String animal, LocalDate dataAdocao, boolean termoAssinado) {
        public static AdocaoResumo of(Adocao a) {
            return new AdocaoResumo(a.getId(), a.getSolicitacao().getId(),
                    a.getSolicitacao().getAnimal().getNome(), a.getDataAdocao(), a.isTermoAssinado());
        }
    }
    public record NovaVisita(@NotNull LocalDate dataVisita, @NotBlank String observacoes,
                             boolean animalBemCuidado) {}
    public record VisitaResumo(Long id, Long adocaoId, LocalDate dataVisita, String observacoes, boolean animalBemCuidado) {
        static VisitaResumo of(VisitaAcompanhamento v) {
            return new VisitaResumo(v.getId(), v.getAdocao().getId(), v.getDataVisita(),
                    v.getObservacoes(), v.isAnimalBemCuidado());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public AdocaoResumo formalizar(@Valid @RequestBody Formalizacao entrada) {
        SolicitacaoAdocao s = solicitacoes.findById(entrada.solicitacaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));
        animais.bloquearPorId(s.getAnimal().getId());
        if (adocoes.findBySolicitacaoId(s.getId()).isPresent()) throw new IllegalStateException("Adoção já formalizada.");
        Adocao a = Adocao.formalizar(s, entrada.termoAssinado(), entrada.observacoes());
        return AdocaoResumo.of(adocoes.saveAndFlush(a));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AdocaoResumo detalhe(@PathVariable Long id) {
        Adocao a = adocoes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adoção não encontrada."));
        return AdocaoResumo.of(a);
    }

    @PostMapping("/{id}/visitas")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('VOLUNTARIO')")
    @Transactional
    public VisitaResumo registrarVisita(@PathVariable Long id, @Valid @RequestBody NovaVisita entrada, Authentication auth) {
        Adocao a = adocoes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adoção não encontrada."));
        Usuario voluntario = usuarios.findByEmail(auth.getName())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        VisitaAcompanhamento visita = a.registrarVisita(voluntario, entrada.dataVisita(),
                entrada.observacoes(), entrada.animalBemCuidado());
        return VisitaResumo.of(visitas.saveAndFlush(visita));
    }

    @GetMapping("/{id}/visitas")
    @Transactional(readOnly = true)
    public List<VisitaResumo> listarVisitas(@PathVariable Long id) {
        if (!adocoes.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Adoção não encontrada.");
        return visitas.findByAdocaoIdOrderByDataVisitaAsc(id).stream().map(VisitaResumo::of).toList();
    }
}

package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.enums.StatusAnimal;
import br.edu.catolicasc.adotapet.repository.*;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@PreAuthorize("hasRole('ADMIN')")
public class RelatorioController {
    private final AnimalRepository animais;
    private final AdocaoRepository adocoes;

    public RelatorioController(AnimalRepository animais, AdocaoRepository adocoes) {
        this.animais = animais; this.adocoes = adocoes;
    }

    @GetMapping("/animais-por-status")
    @Transactional(readOnly = true)
    public Map<StatusAnimal, Long> animaisPorStatus() {
        Map<StatusAnimal, Long> totais = new EnumMap<>(StatusAnimal.class);
        for (StatusAnimal status : StatusAnimal.values())
            totais.put(status, animais.countByStatus(status));
        return totais;
    }

    @GetMapping("/adocoes-por-periodo")
    @Transactional(readOnly = true)
    public Map<String, Object> adocoesPorPeriodo(@RequestParam LocalDate de, @RequestParam LocalDate ate) {
        if (ate.isBefore(de)) throw new IllegalArgumentException("Data final anterior à inicial.");
        long total = adocoes.findByDataAdocaoBetween(de, ate).size();
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("de", de);
        resposta.put("ate", ate);
        resposta.put("total", total);
        return resposta;
    }
}

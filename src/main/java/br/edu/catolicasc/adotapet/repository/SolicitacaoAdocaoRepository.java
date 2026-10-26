package br.edu.catolicasc.adotapet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;

public interface SolicitacaoAdocaoRepository extends JpaRepository<SolicitacaoAdocao, Long> {

    List<SolicitacaoAdocao> findByAdotanteId(Long adotanteId);

    List<SolicitacaoAdocao> findByAnimalId(Long animalId);

    List<SolicitacaoAdocao> findByStatus(StatusSolicitacao status);

    boolean existsByAdotanteIdAndAnimalIdAndStatusIn(Long adotanteId, Long animalId, Collection<StatusSolicitacao> status);
}

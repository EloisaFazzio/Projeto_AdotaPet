package br.edu.catolicasc.adotapet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;

public interface VacinacaoAnimalRepository extends JpaRepository<VacinacaoAnimal, Long> {

    List<VacinacaoAnimal> findByAnimalIdOrderByDataAplicacaoDesc(Long animalId);

    List<VacinacaoAnimal> findByProximaDoseBefore(LocalDate data);
}

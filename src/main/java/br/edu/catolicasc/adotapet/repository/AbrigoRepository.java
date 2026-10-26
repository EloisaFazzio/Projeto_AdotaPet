package br.edu.catolicasc.adotapet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;

public interface AbrigoRepository extends JpaRepository<Abrigo, Long> {

    List<Abrigo> findByCidadeIgnoreCase(String cidade);

    List<Abrigo> findByVoluntariosId(Long voluntarioId);
}

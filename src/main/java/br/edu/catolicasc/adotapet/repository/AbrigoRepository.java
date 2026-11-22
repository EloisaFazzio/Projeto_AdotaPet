package br.edu.catolicasc.adotapet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AbrigoRepository extends JpaRepository<Abrigo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Abrigo a where a.id = :id")
    Optional<Abrigo> bloquearPorId(@Param("id") Long id);


    List<Abrigo> findByCidadeIgnoreCase(String cidade);

    List<Abrigo> findByVoluntariosId(Long voluntarioId);
}

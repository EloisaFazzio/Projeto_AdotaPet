package br.edu.catolicasc.adotapet.repository;

import br.edu.catolicasc.adotapet.model.Animal;
import br.edu.catolicasc.adotapet.model.enums.Especie;
import br.edu.catolicasc.adotapet.model.enums.StatusAnimal;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {
    List<Animal> findByStatus(StatusAnimal status);
    long countByStatus(StatusAnimal status);
    List<Animal> findByEspecieAndStatus(Especie especie, StatusAnimal status);
    List<Animal> findByAbrigoId(Long abrigoId);
    long countByAbrigoIdAndStatusNot(Long abrigoId, StatusAnimal status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Animal a where a.id = :id")
    Optional<Animal> bloquearPorId(@Param("id") Long id);
}

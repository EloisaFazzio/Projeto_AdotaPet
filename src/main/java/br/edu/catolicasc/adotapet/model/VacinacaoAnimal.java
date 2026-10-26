package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Entidade associativa Animal x Vacina (N:M), com data de aplicação e próxima dose. */
@Entity
@Table(name = "vacinacao_animal")
public class VacinacaoAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacina_id", nullable = false)
    private Vacina vacina;

    @NotNull
    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "proxima_dose")
    private LocalDate proximaDose;

    @Column(length = 120)
    private String veterinario;

    public VacinacaoAnimal() {
        // exigido pelo JPA
    }


    /** Indica se a próxima dose já deveria ter sido aplicada em relação à data informada. */
    public boolean estaVencida(LocalDate hoje) {
        return proximaDose != null && proximaDose.isBefore(hoje);
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Vacina getVacina() {
        return vacina;
    }

    public void setVacina(Vacina vacina) {
        this.vacina = vacina;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getProximaDose() {
        return proximaDose;
    }

    public void setProximaDose(LocalDate proximaDose) {
        this.proximaDose = proximaDose;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

}

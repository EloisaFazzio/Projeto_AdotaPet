package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Visita ou contato de acompanhamento pós-adoção realizado por um voluntário. */
@Entity
@Table(name = "visita_acompanhamento")
public class VisitaAcompanhamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adocao_id", nullable = false)
    private Adocao adocao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Usuario voluntario;

    @NotNull
    @Column(name = "data_visita", nullable = false)
    private LocalDate dataVisita;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String observacoes;

    /** Avaliação do voluntário sobre os cuidados com o animal. */
    @Column(name = "animal_bem_cuidado", nullable = false)
    private boolean animalBemCuidado = true;

    public VisitaAcompanhamento() {
        // exigido pelo JPA
    }


    /** Sinaliza visitas em que o voluntário identificou falta de cuidados. */
    public boolean exigeAtencao() {
        return !animalBemCuidado;
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public Adocao getAdocao() {
        return adocao;
    }

    public void setAdocao(Adocao adocao) {
        this.adocao = adocao;
    }

    public Usuario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Usuario voluntario) {
        this.voluntario = voluntario;
    }

    public LocalDate getDataVisita() {
        return dataVisita;
    }

    public void setDataVisita(LocalDate dataVisita) {
        this.dataVisita = dataVisita;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isAnimalBemCuidado() {
        return animalBemCuidado;
    }

    public void setAnimalBemCuidado(boolean animalBemCuidado) {
        this.animalBemCuidado = animalBemCuidado;
    }

}

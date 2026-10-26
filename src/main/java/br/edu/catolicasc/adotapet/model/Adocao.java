package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Adoção formalizada, com a assinatura do termo de responsabilidade. */
@Entity
@Table(name = "adocao")
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false, unique = true)
    private SolicitacaoAdocao solicitacao;

    @Column(name = "data_adocao", nullable = false)
    private LocalDate dataAdocao = LocalDate.now();

    @Column(name = "termo_assinado", nullable = false)
    private boolean termoAssinado;

    @Column(length = 500)
    private String observacoes;

    @OneToMany(mappedBy = "adocao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitaAcompanhamento> visitas = new ArrayList<>();

    public Adocao() {
        // exigido pelo JPA
    }


    /** RN06: só solicitações aprovadas, com termo assinado, viram adoção; o animal passa a ADOTADO. */
    public static Adocao formalizar(SolicitacaoAdocao solicitacao, boolean termoAssinado, String observacoes) {
        if (solicitacao.getStatus() != StatusSolicitacao.APROVADA) {
            throw new IllegalStateException("Só é possível formalizar solicitações aprovadas.");
        }
        if (!termoAssinado) {
            throw new IllegalArgumentException("O termo de responsabilidade deve estar assinado.");
        }
        solicitacao.getAnimal().marcarComoAdotado();
        Adocao adocao = new Adocao();
        adocao.solicitacao = solicitacao;
        adocao.termoAssinado = true;
        adocao.observacoes = observacoes;
        return adocao;
    }

    public VisitaAcompanhamento registrarVisita(Usuario voluntario, LocalDate dataVisita, String observacoes, boolean animalBemCuidado) {
        VisitaAcompanhamento visita = new VisitaAcompanhamento();
        visita.setAdocao(this);
        visita.setVoluntario(voluntario);
        visita.setDataVisita(dataVisita);
        visita.setObservacoes(observacoes);
        visita.setAnimalBemCuidado(animalBemCuidado);
        visitas.add(visita);
        return visita;
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public SolicitacaoAdocao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoAdocao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public LocalDate getDataAdocao() {
        return dataAdocao;
    }

    public void setDataAdocao(LocalDate dataAdocao) {
        this.dataAdocao = dataAdocao;
    }

    public boolean isTermoAssinado() {
        return termoAssinado;
    }

    public void setTermoAssinado(boolean termoAssinado) {
        this.termoAssinado = termoAssinado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<VisitaAcompanhamento> getVisitas() {
        return visitas;
    }

}

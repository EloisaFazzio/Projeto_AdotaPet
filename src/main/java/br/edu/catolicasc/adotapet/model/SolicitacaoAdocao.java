package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Pedido de adoção feito por um adotante para um animal; passa por análise da ONG. */
@Entity
@Table(name = "solicitacao_adocao")
public class SolicitacaoAdocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adotante_id", nullable = false)
    private Usuario adotante;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    /** Voluntário ou administrador que analisou o pedido (nulo enquanto pendente). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analisado_por_id")
    private Usuario analisadoPor;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String justificativa;

    @Column(length = 1000)
    private String parecer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @Column(name = "solicitada_em", nullable = false)
    private LocalDateTime solicitadaEm = LocalDateTime.now();

    @Column(name = "analisada_em")
    private LocalDateTime analisadaEm;

    /** Adoção formalizada a partir desta solicitação (só existe se aprovada e concluída). */
    @OneToOne(mappedBy = "solicitacao")
    private Adocao adocao;

    public SolicitacaoAdocao() {
        // exigido pelo JPA
    }


    public boolean estaAtiva() {
        return status == StatusSolicitacao.PENDENTE || status == StatusSolicitacao.EM_ANALISE;
    }

    /** RN03 e RN08: o analista inicia a análise e o animal entra em processo de adoção. */
    public void iniciarAnalise(Usuario analista) {
        exigirAnalista(analista);
        if (status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível analisar solicitações pendentes.");
        }
        animal.iniciarProcessoAdocao();
        this.analisadoPor = analista;
        this.status = StatusSolicitacao.EM_ANALISE;
    }

    public void aprovar(Usuario analista, String parecer) {
        exigirAnalista(analista);
        if (status != StatusSolicitacao.EM_ANALISE) {
            throw new IllegalStateException("Só é possível aprovar solicitações em análise.");
        }
        concluirAnalise(analista, parecer, StatusSolicitacao.APROVADA);
    }

    public void rejeitar(Usuario analista, String parecer) {
        exigirAnalista(analista);
        if (!estaAtiva()) {
            throw new IllegalStateException("Só é possível rejeitar solicitações ativas.");
        }
        concluirAnalise(analista, parecer, StatusSolicitacao.REJEITADA);
    }

    /** RF08: o adotante só pode cancelar enquanto o pedido não foi analisado. */
    public void cancelar() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível cancelar solicitações pendentes.");
        }
        this.status = StatusSolicitacao.CANCELADA;
    }

    private void concluirAnalise(Usuario analista, String parecer, StatusSolicitacao novoStatus) {
        this.analisadoPor = analista;
        this.parecer = parecer;
        this.status = novoStatus;
        this.analisadaEm = LocalDateTime.now();
    }

    private static void exigirAnalista(Usuario analista) {
        if (analista == null || !analista.podeAnalisarSolicitacoes()) {
            throw new IllegalArgumentException("Apenas voluntários ou administradores ativos podem analisar solicitações.");
        }
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public Usuario getAdotante() {
        return adotante;
    }

    public void setAdotante(Usuario adotante) {
        this.adotante = adotante;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Usuario getAnalisadoPor() {
        return analisadoPor;
    }

    public void setAnalisadoPor(Usuario analisadoPor) {
        this.analisadoPor = analisadoPor;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public LocalDateTime getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(LocalDateTime solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }

    public LocalDateTime getAnalisadaEm() {
        return analisadaEm;
    }

    public void setAnalisadaEm(LocalDateTime analisadaEm) {
        this.analisadaEm = analisadaEm;
    }

    public Adocao getAdocao() {
        return adocao;
    }

    public void setAdocao(Adocao adocao) {
        this.adocao = adocao;
    }

}

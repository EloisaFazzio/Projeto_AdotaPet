package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Animal acolhido pela ONG e disponível (ou não) para adoção. */
@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especie especie;

    @Column(length = 80)
    private String raca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Porte porte;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_resgate", nullable = false)
    private LocalDate dataResgate = LocalDate.now();

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private boolean castrado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatusAnimal status = StatusAnimal.DISPONIVEL;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "abrigo_id", nullable = false)
    private Abrigo abrigo;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VacinacaoAnimal> vacinacoes = new ArrayList<>();

    @OneToMany(mappedBy = "animal")
    private List<SolicitacaoAdocao> solicitacoes = new ArrayList<>();

    public Animal() {
        // exigido pelo JPA
    }


    public boolean estaDisponivel() {
        return status == StatusAnimal.DISPONIVEL;
    }

    /** RN09: a próxima dose, quando informada, deve ser posterior à aplicação. */
    public VacinacaoAnimal registrarVacinacao(Vacina vacina, LocalDate dataAplicacao, LocalDate proximaDose) {
        if (proximaDose != null && !proximaDose.isAfter(dataAplicacao)) {
            throw new IllegalArgumentException("A próxima dose deve ser posterior à data de aplicação.");
        }
        VacinacaoAnimal vacinacao = new VacinacaoAnimal();
        vacinacao.setAnimal(this);
        vacinacao.setVacina(vacina);
        vacinacao.setDataAplicacao(dataAplicacao);
        vacinacao.setProximaDose(proximaDose);
        vacinacoes.add(vacinacao);
        return vacinacao;
    }

    /** RN03: o animal entra em processo de adoção quando uma solicitação passa a ser analisada. */
    public void iniciarProcessoAdocao() {
        if (status != StatusAnimal.DISPONIVEL && status != StatusAnimal.EM_PROCESSO_ADOCAO) {
            throw new IllegalStateException("Animal não está disponível para adoção: " + status);
        }
        this.status = StatusAnimal.EM_PROCESSO_ADOCAO;
    }

    /** RN05: sem solicitações ativas, o animal volta a ficar disponível. */
    public void liberarParaAdocao() {
        if (status == StatusAnimal.ADOTADO) {
            throw new IllegalStateException("Animal já foi adotado.");
        }
        this.status = StatusAnimal.DISPONIVEL;
    }

    /** RN06: ao formalizar a adoção, o animal passa a ADOTADO. */
    public void marcarComoAdotado() {
        if (status != StatusAnimal.EM_PROCESSO_ADOCAO) {
            throw new IllegalStateException("A adoção só pode ser concluída para animal em processo de adoção.");
        }
        this.status = StatusAnimal.ADOTADO;
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Porte getPorte() {
        return porte;
    }

    public void setPorte(Porte porte) {
        this.porte = porte;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataResgate() {
        return dataResgate;
    }

    public void setDataResgate(LocalDate dataResgate) {
        this.dataResgate = dataResgate;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isCastrado() {
        return castrado;
    }

    public void setCastrado(boolean castrado) {
        this.castrado = castrado;
    }

    public StatusAnimal getStatus() {
        return status;
    }

    public void setStatus(StatusAnimal status) {
        this.status = status;
    }

    public Abrigo getAbrigo() {
        return abrigo;
    }

    public void setAbrigo(Abrigo abrigo) {
        this.abrigo = abrigo;
    }

    public List<VacinacaoAnimal> getVacinacoes() {
        return vacinacoes;
    }

    public List<SolicitacaoAdocao> getSolicitacoes() {
        return solicitacoes;
    }

}

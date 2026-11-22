package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Catálogo de vacinas que podem ser aplicadas aos animais. */
@Entity
@Table(name = "vacina")
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    /** Espécie à qual a vacina se destina. */
    @Enumerated(EnumType.STRING)
    @Column(name = "especie_alvo", nullable = false, length = 20)
    private Especie especieAlvo;

    /** Intervalo recomendado entre doses, em dias (nulo para dose única). */
    @Column(name = "intervalo_dias")
    private Integer intervaloDias;

    /** Imagem de apresentação associada ao cadastro da vacina, armazenada no PostgreSQL. */
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "imagem_dados", columnDefinition = "bytea")
    private byte[] imagemDados;

    @Column(name = "imagem_mime", length = 24)
    private String imagemMime;

    @OneToMany(mappedBy = "vacina")
    private List<VacinacaoAnimal> aplicacoes = new ArrayList<>();

    public Vacina() {
        // exigido pelo JPA
    }


    /** Sugere a data da próxima dose a partir da aplicação; nulo quando a vacina é de dose única. */
    public LocalDate calcularProximaDose(LocalDate dataAplicacao) {
        return intervaloDias == null ? null : dataAplicacao.plusDays(intervaloDias);
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

    public Especie getEspecieAlvo() {
        return especieAlvo;
    }

    public void setEspecieAlvo(Especie especieAlvo) {
        this.especieAlvo = especieAlvo;
    }

    public Integer getIntervaloDias() {
        return intervaloDias;
    }

    public void setIntervaloDias(Integer intervaloDias) {
        this.intervaloDias = intervaloDias;
    }

    public byte[] getImagemDados() { return imagemDados == null ? null : imagemDados.clone(); }
    public void setImagemDados(byte[] dados) { this.imagemDados = dados == null ? null : dados.clone(); }
    public String getImagemMime() { return imagemMime; }
    public void setImagemMime(String mime) { this.imagemMime = mime; }

    public List<VacinacaoAnimal> getAplicacoes() {
        return aplicacoes;
    }

}

package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Abrigo ou lar temporário mantido pela ONG. */
@Entity
@Table(name = "abrigo")
public class Abrigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String endereco;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String cidade;

    @NotBlank
    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 20)
    private String telefone;

    /** Quantidade máxima de animais que o abrigo comporta. */
    @Min(1)
    @Column(nullable = false)
    private int capacidade;

    @OneToMany(mappedBy = "abrigo")
    private List<Animal> animais = new ArrayList<>();

    /** Voluntários que atuam neste abrigo (lado inverso do N:M). */
    @ManyToMany(mappedBy = "abrigos")
    private Set<Usuario> voluntarios = new HashSet<>();

    public Abrigo() {
        // exigido pelo JPA
    }


    /** RN07: indica se ainda há vaga, dado o número de animais não adotados já abrigados. */
    public boolean possuiVaga(long animaisAbrigados) {
        return animaisAbrigados < capacidade;
    }

    public void adicionarVoluntario(Usuario voluntario) {
        voluntarios.add(voluntario);
        voluntario.getAbrigos().add(this);
    }

    public void removerVoluntario(Usuario voluntario) {
        voluntarios.remove(voluntario);
        voluntario.getAbrigos().remove(this);
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public Set<Usuario> getVoluntarios() {
        return voluntarios;
    }

}

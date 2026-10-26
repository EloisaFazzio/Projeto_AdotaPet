package br.edu.catolicasc.adotapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import br.edu.catolicasc.adotapet.model.enums.*;

/** Usuário do sistema (administrador, voluntário ou adotante), definido pelo {@link Perfil}. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 160)
    private String email;

    /** Hash BCrypt da senha; nunca armazenar texto puro. */
    @NotBlank
    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(length = 20)
    private String telefone;

    @Column(length = 80)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    /** Solicitações feitas por este usuário (quando adotante). */
    @OneToMany(mappedBy = "adotante")
    private List<SolicitacaoAdocao> solicitacoes = new ArrayList<>();

    /** Abrigos em que o voluntário atua (N:M com Abrigo). */
    @ManyToMany
    @JoinTable(name = "voluntario_abrigo",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "abrigo_id"))
    private Set<Abrigo> abrigos = new HashSet<>();

    public Usuario() {
        // exigido pelo JPA
    }


    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public boolean isVoluntario() {
        return perfil == Perfil.VOLUNTARIO;
    }

    public boolean isAdotante() {
        return perfil == Perfil.ADOTANTE;
    }

    /** RN08: apenas voluntários e administradores analisam solicitações. */
    public boolean podeAnalisarSolicitacoes() {
        return ativo && (perfil == Perfil.VOLUNTARIO || perfil == Perfil.ADMIN);
    }

    public void desativar() {
        this.ativo = false;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<SolicitacaoAdocao> getSolicitacoes() {
        return solicitacoes;
    }

    public Set<Abrigo> getAbrigos() {
        return abrigos;
    }

}

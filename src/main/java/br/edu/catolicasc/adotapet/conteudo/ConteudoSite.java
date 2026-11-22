package br.edu.catolicasc.adotapet.conteudo;
import jakarta.persistence.*;

@Entity
@Table(name="conteudo_site")
public class ConteudoSite {
    @Id private String chave;
    @Column(nullable=false, length=3000) private String valor;
    protected ConteudoSite() {}
    public ConteudoSite(String chave,String valor) {this.chave=chave;this.valor=valor;}
    public String getChave(){return chave;}
    public String getValor(){return valor;}
    public void setValor(String valor){this.valor=valor;}
}

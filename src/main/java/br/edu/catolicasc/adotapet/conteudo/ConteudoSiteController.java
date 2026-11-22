package br.edu.catolicasc.adotapet.conteudo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/conteudo")
public class ConteudoSiteController {
    private static final Map<String,String> PADRAO = Map.of(
        "titulo", "Um novo amigo. Um amor para a vida inteira.",
        "apresentacao", "Conectamos animais acolhidos por ONGs a pessoas prontas para oferecer cuidado e um lar seguro.",
        "missao", "Apoiar ONGs e pessoas na adoção responsável, com transparência e acompanhamento.",
        "imagemHero", "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=900&q=85",
        "imagemMissao", "https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=1000&q=85"
    );
    private final ConteudoSiteRepository repositorio;
    public ConteudoSiteController(ConteudoSiteRepository repositorio) {this.repositorio=repositorio;}
    public record Alteracao(@NotBlank @Size(max=3000) String valor) {}

    @GetMapping
    @Transactional(readOnly=true)
    public Map<String,String> publico() {
        Map<String,String> valores=new LinkedHashMap<>(PADRAO);
        repositorio.findAll().forEach(c -> {if(PADRAO.containsKey(c.getChave())) valores.put(c.getChave(),c.getValor());});
        return valores;
    }
    @PutMapping("/{chave}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Map<String,String> alterar(@PathVariable String chave,@Valid @RequestBody Alteracao entrada) {
        if(!PADRAO.containsKey(chave)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Campo desconhecido.");
        String valor=entrada.valor().trim();
        if(valor.isBlank()) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha o conteúdo.");
        if(chave.startsWith("imagem") && !valor.matches("https://[^\\s<>\\\"]{1,2000}"))
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Use uma URL HTTPS válida para a imagem.");
        repositorio.save(new ConteudoSite(chave,valor));
        return Map.of(chave,valor);
    }
}

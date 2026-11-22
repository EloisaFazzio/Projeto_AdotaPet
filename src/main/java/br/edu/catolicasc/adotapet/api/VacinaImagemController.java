package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Vacina;
import br.edu.catolicasc.adotapet.repository.VacinaRepository;
import java.io.IOException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** Upload seguro de imagem da vacina: PostgreSQL, restrito à equipe autenticada. */
@RestController
@RequestMapping("/api/vacinas")
public class VacinaImagemController {
    private final VacinaRepository vacinas;
    public VacinaImagemController(VacinaRepository vacinas) { this.vacinas = vacinas; }

    @PutMapping(value="/{id}/imagem", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> anexar(@PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        Vacina vacina = buscar(id);
        if (arquivo.isEmpty() || arquivo.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Escolha uma imagem de até 2 MB.");
        }
        byte[] dados = arquivo.getBytes();
        String tipo = identificarImagem(dados);
        if (tipo == null) {
            throw new IllegalArgumentException("Use uma imagem válida PNG, JPEG ou WebP.");
        }
        vacina.setImagemDados(dados);
        vacina.setImagemMime(tipo);
        vacinas.save(vacina);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/imagem")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional(readOnly=true)
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        Vacina vacina=buscar(id);
        if(vacina.getImagemDados()==null || vacina.getImagemMime()==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Vacina sem imagem cadastrada.");
        }
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .header("X-Content-Type-Options", "nosniff")
            .contentType(MediaType.parseMediaType(vacina.getImagemMime()))
            .body(vacina.getImagemDados());
    }

    private Vacina buscar(Long id) {
        return vacinas.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Vacina não encontrada."));
    }

    private static String identificarImagem(byte[] a) {
        if(a.length >= 8 && (a[0]&255)==137 && a[1]==80 && a[2]==78 && a[3]==71
                && a[4]==13 && a[5]==10 && a[6]==26 && a[7]==10) return "image/png";
        if(a.length >= 3 && (a[0]&255)==255 && (a[1]&255)==216 && (a[2]&255)==255) return "image/jpeg";
        if(a.length >= 12 && a[0]==82 && a[1]==73 && a[2]==70 && a[3]==70
                && a[8]==87 && a[9]==69 && a[10]==66 && a[11]==80) return "image/webp";
        return null;
    }
}

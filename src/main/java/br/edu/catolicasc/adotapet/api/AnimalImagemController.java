package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.Animal;
import br.edu.catolicasc.adotapet.repository.AnimalRepository;
import java.io.IOException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** Foto cadastrada no PostgreSQL. Upload protegido e leitura pública do retrato. */
@RestController
@RequestMapping("/api/animais")
public class AnimalImagemController {
    private final AnimalRepository animais;
    public AnimalImagemController(AnimalRepository animais) {this.animais = animais;}

    @PutMapping(value="/{id}/imagem", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public ResponseEntity<Void> anexar(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo)
            throws IOException {
        Animal animal = buscar(id);
        if (arquivo.isEmpty() || arquivo.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Escolha uma foto de até 2 MB.");
        }
        byte[] bytes = arquivo.getBytes();
        String tipo = identificarImagem(bytes);
        if (tipo == null) throw new IllegalArgumentException("Use PNG, JPEG ou WebP válido.");
        animal.setImagemDados(bytes);
        animal.setImagemMime(tipo);
        animais.save(animal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/imagem")
    @Transactional(readOnly=true)
    public ResponseEntity<byte[]> consultar(@PathVariable Long id) {
        Animal animal = buscar(id);
        if (animal.getImagemDados()==null || animal.getImagemMime()==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Animal sem foto anexada.");
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .header("X-Content-Type-Options","nosniff")
            .contentType(MediaType.parseMediaType(animal.getImagemMime()))
            .body(animal.getImagemDados());
    }

    @DeleteMapping("/{id}/imagem")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        Animal animal = buscar(id);
        animal.setImagemDados(null);
        animal.setImagemMime(null);
        animais.save(animal);
        return ResponseEntity.noContent().build();
    }

    private Animal buscar(Long id) {
        return animais.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Animal não encontrado."));
    }

    private static String identificarImagem(byte[] a) {
        if (a.length>=8 && (a[0]&255)==137 && a[1]==80 && a[2]==78 && a[3]==71
            && a[4]==13 && a[5]==10 && a[6]==26 && a[7]==10) return "image/png";
        if (a.length>=3 && (a[0]&255)==255 && (a[1]&255)==216 && (a[2]&255)==255) return "image/jpeg";
        if (a.length>=12 && a[0]==82 && a[1]==73 && a[2]==70 && a[3]==70
            && a[8]==87 && a[9]==69 && a[10]==66 && a[11]==80) return "image/webp";
        return null;
    }
}

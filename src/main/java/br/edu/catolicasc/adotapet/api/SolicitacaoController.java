package br.edu.catolicasc.adotapet.api;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;
import br.edu.catolicasc.adotapet.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SolicitacaoController {
    private final SolicitacaoAdocaoRepository solicitacoes;
    private final AnimalRepository animais;
    private final UsuarioRepository usuarios;

    public SolicitacaoController(SolicitacaoAdocaoRepository solicitacoes, AnimalRepository animais, UsuarioRepository usuarios) {
        this.solicitacoes = solicitacoes; this.animais = animais; this.usuarios = usuarios;
    }

    public record NovoPedido(@NotNull Long animalId, @NotBlank String justificativa) {}
    public record Parecer(@NotBlank String parecer) {}
    public record PedidoResumo(Long id, Long animalId, String animal, String adotante,
                               StatusSolicitacao status, String justificativa, String parecer, LocalDateTime solicitadaEm) {
        public static PedidoResumo of(SolicitacaoAdocao s) {
            return new PedidoResumo(s.getId(), s.getAnimal().getId(), s.getAnimal().getNome(),
                    s.getAdotante().getNome(), s.getStatus(), s.getJustificativa(), s.getParecer(), s.getSolicitadaEm());
        }
    }

    private Usuario usuario(Authentication auth) {
        return usuarios.findByEmail(auth.getName())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
    private SolicitacaoAdocao pedido(Long id) {
        return solicitacoes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));
    }

    @PostMapping("/solicitacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADOTANTE')")
    @Transactional
    public PedidoResumo solicitar(@Valid @RequestBody NovoPedido entrada, Authentication auth) {
        Usuario adotante = usuario(auth);
        // Bloqueio serializa o teste de solicitação duplicada para o mesmo animal.
        Animal animal = animais.bloquearPorId(entrada.animalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));
        if (!animal.estaDisponivel()) throw new IllegalStateException("O animal não está disponível para adoção.");
        if (solicitacoes.existsByAdotanteIdAndAnimalIdAndStatusIn(adotante.getId(), animal.getId(),
                Set.of(StatusSolicitacao.PENDENTE, StatusSolicitacao.EM_ANALISE))) {
            throw new IllegalStateException("Você já possui uma solicitação ativa para este animal.");
        }
        SolicitacaoAdocao s = new SolicitacaoAdocao();
        s.setAdotante(adotante); s.setAnimal(animal); s.setJustificativa(entrada.justificativa().trim());
        return PedidoResumo.of(solicitacoes.saveAndFlush(s));
    }

    @GetMapping("/usuarios/me/solicitacoes")
    @PreAuthorize("hasRole('ADOTANTE')")
    @Transactional(readOnly = true)
    public List<PedidoResumo> meusPedidos(Authentication auth) {
        return solicitacoes.findByAdotanteId(usuario(auth).getId()).stream()
                .sorted(Comparator.comparing(SolicitacaoAdocao::getSolicitadaEm).reversed())
                .map(PedidoResumo::of).toList();
    }

    @GetMapping("/solicitacoes")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional(readOnly = true)
    public List<PedidoResumo> listar(@RequestParam(required = false) StatusSolicitacao status) {
        List<SolicitacaoAdocao> pedidos = status == null ? solicitacoes.findAll() : solicitacoes.findByStatus(status);
        return pedidos.stream().sorted(Comparator.comparing(SolicitacaoAdocao::getSolicitadaEm).reversed())
                .map(PedidoResumo::of).toList();
    }

    /** RF08: adotante só pode consultar o próprio pedido; equipe pode consultar qualquer pedido. */
    @GetMapping("/solicitacoes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO','ADOTANTE')")
    @Transactional(readOnly = true)
    public PedidoResumo consultar(@PathVariable Long id, Authentication auth) {
        SolicitacaoAdocao s = pedido(id);
        Usuario atual = usuario(auth);
        if (atual.getPerfil() == Perfil.ADOTANTE && !s.getAdotante().getId().equals(atual.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solicitação pertence a outro adotante.");
        }
        return PedidoResumo.of(s);
    }

    /** RF08: evita que alguém consulte solicitações de outro adotante via ID de usuário. */
    @GetMapping("/usuarios/{id}/solicitacoes")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO','ADOTANTE')")
    @Transactional(readOnly = true)
    public List<PedidoResumo> porUsuario(@PathVariable Long id, Authentication auth) {
        Usuario atual = usuario(auth);
        if (atual.getPerfil() == Perfil.ADOTANTE && !atual.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido consultar pedidos de outro adotante.");
        }
        if (!usuarios.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }
        return solicitacoes.findByAdotanteId(id).stream()
                .sorted(Comparator.comparing(SolicitacaoAdocao::getSolicitadaEm).reversed())
                .map(PedidoResumo::of).toList();
    }

    @PatchMapping("/solicitacoes/{id}/cancelar")
    @PreAuthorize("hasRole('ADOTANTE')")
    @Transactional
    public PedidoResumo cancelar(@PathVariable Long id, Authentication auth) {
        SolicitacaoAdocao s = pedido(id);
        if (!s.getAdotante().getId().equals(usuario(auth).getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solicitação pertence a outro adotante.");
        animais.bloquearPorId(s.getAnimal().getId());
        s.cancelar();
        liberarSePossivel(s.getAnimal());
        return PedidoResumo.of(s);
    }

    @PatchMapping("/solicitacoes/{id}/analisar")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public PedidoResumo analisar(@PathVariable Long id, Authentication auth) {
        SolicitacaoAdocao s = pedido(id);
        animais.bloquearPorId(s.getAnimal().getId());
        s.iniciarAnalise(usuario(auth));
        return PedidoResumo.of(s);
    }

    @PatchMapping("/solicitacoes/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public PedidoResumo aprovar(@PathVariable Long id, @Valid @RequestBody Parecer parecer, Authentication auth) {
        SolicitacaoAdocao s = pedido(id);
        animais.bloquearPorId(s.getAnimal().getId());
        Usuario analista = usuario(auth);
        s.aprovar(analista, parecer.parecer());
        // RN04: todas as demais solicitações ativas do animal são rejeitadas.
        for (SolicitacaoAdocao outra : solicitacoes.findByAnimalId(s.getAnimal().getId())) {
            if (!outra.getId().equals(s.getId()) && outra.estaAtiva())
                outra.rejeitar(analista, "Outra solicitação foi aprovada para este animal.");
        }
        return PedidoResumo.of(s);
    }

    @PatchMapping("/solicitacoes/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('ADMIN','VOLUNTARIO')")
    @Transactional
    public PedidoResumo rejeitar(@PathVariable Long id, @Valid @RequestBody Parecer parecer, Authentication auth) {
        SolicitacaoAdocao s = pedido(id);
        animais.bloquearPorId(s.getAnimal().getId());
        s.rejeitar(usuario(auth), parecer.parecer());
        liberarSePossivel(s.getAnimal());
        return PedidoResumo.of(s);
    }

    private void liberarSePossivel(Animal animal) {
        boolean existeEmAndamentoOuAprovada = solicitacoes.findByAnimalId(animal.getId()).stream()
                .anyMatch(s -> s.estaAtiva() || s.getStatus() == StatusSolicitacao.APROVADA);
        if (!existeEmAndamentoOuAprovada && animal.getStatus() == StatusAnimal.EM_PROCESSO_ADOCAO)
            animal.liberarParaAdocao();
    }
}

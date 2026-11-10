package br.edu.catolicasc.adotapet;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegrasDominioTest {
    @Test
    void proximaDoseNaoPodeSerAnteriorOuIgualAplicacao() {
        Animal animal = new Animal();
        Vacina vacina = new Vacina();
        LocalDate hoje = LocalDate.now();
        assertThrows(IllegalArgumentException.class,
                () -> animal.registrarVacinacao(vacina, hoje, hoje));
        assertThrows(IllegalArgumentException.class,
                () -> animal.registrarVacinacao(vacina, hoje, hoje.minusDays(1)));
        assertNotNull(animal.registrarVacinacao(vacina, hoje, hoje.plusDays(30)));
    }

    @Test
    void solicitacaoPendentePodeSerCanceladaApenasUmaVez() {
        SolicitacaoAdocao solicitacao = new SolicitacaoAdocao();
        assertTrue(solicitacao.estaAtiva());
        solicitacao.cancelar();
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
        assertFalse(solicitacao.estaAtiva());
        assertThrows(IllegalStateException.class, solicitacao::cancelar);
    }

    @Test
    void formalizacaoExigeTermoEAtualizaStatusDoAnimal() {
        Animal animal = new Animal();
        SolicitacaoAdocao solicitacao = new SolicitacaoAdocao();
        solicitacao.setAnimal(animal);
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        animal.iniciarProcessoAdocao();
        assertThrows(IllegalArgumentException.class,
                () -> Adocao.formalizar(solicitacao, false, "sem assinatura"));
        Adocao adocao = Adocao.formalizar(solicitacao, true, "termo assinado");
        assertTrue(adocao.isTermoAssinado());
        assertEquals(StatusAnimal.ADOTADO, animal.getStatus());
    }

    @Test
    void apenasEquipeAtivaPodeAnalisar() {
        Usuario usuario = new Usuario();
        usuario.setPerfil(Perfil.ADMIN);
        assertTrue(usuario.podeAnalisarSolicitacoes());
        usuario.desativar();
        assertFalse(usuario.podeAnalisarSolicitacoes());
        usuario.setAtivo(true);
        usuario.setPerfil(Perfil.ADOTANTE);
        assertFalse(usuario.podeAnalisarSolicitacoes());
    }
}

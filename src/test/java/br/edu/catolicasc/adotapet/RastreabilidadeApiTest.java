package br.edu.catolicasc.adotapet;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;
import br.edu.catolicasc.adotapet.repository.*;
import br.edu.catolicasc.adotapet.security.JwtService;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RastreabilidadeApiTest {
    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired AnimalRepository animais;
    @Autowired AbrigoRepository abrigos;
    @Autowired SolicitacaoAdocaoRepository solicitacoes;
    @Autowired VacinacaoAnimalRepository vacinacoes;
    @Autowired VacinaRepository vacinas;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;

    private Usuario usuario(Perfil perfil) {
        Usuario u = new Usuario();
        u.setNome("Usuario Teste");
        u.setEmail("teste-" + UUID.randomUUID() + "@exemplo.com");
        u.setSenhaHash(encoder.encode("SenhaForte123!"));
        u.setPerfil(perfil);
        u.setAtivo(true);
        return usuarios.saveAndFlush(u);
    }

    private String token(Usuario usuario) { return "Bearer " + jwt.gerar(usuario.getEmail()); }

    private Animal animal() {
        Abrigo abrigo = new Abrigo();
        abrigo.setNome("Abrigo de Teste");
        abrigo.setCidade("Jaraguá do Sul");
        abrigo.setEndereco("Rua Central, 88");
        abrigo.setUf("SC");
        abrigo.setCapacidade(5);
        abrigos.saveAndFlush(abrigo);
        Animal animal = new Animal();
        animal.setNome("Pipoca");
        animal.setAbrigo(abrigo);
        animal.setEspecie(Especie.CAO);
        animal.setPorte(Porte.PEQUENO);
        animal.setSexo(Sexo.FEMEA);
        return animais.saveAndFlush(animal);
    }

    @Test
    @Transactional
    void swaggerEstaDisponivelEmDesenvolvimentoSemToken() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("AdotaPet — API REST"));
    }

    @Test
    @Transactional
    void exclusaoDeAnimalSemHistoricoExigeEquipe() throws Exception {
        Animal a = animal();
        Usuario adotante = usuario(Perfil.ADOTANTE);
        Usuario admin = usuario(Perfil.ADMIN);

        mvc.perform(delete("/api/animais/{id}", a.getId())
                .header("Authorization", token(adotante)))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/animais/{id}", a.getId())
                .header("Authorization", token(admin)))
                .andExpect(status().isNoContent());
        assertFalse(animais.existsById(a.getId()));
    }

    @Test
    @Transactional
    void animalComHistoricoDeVacinaNaoPodeSerRemovido() throws Exception {
        Animal a = animal();
        Usuario admin = usuario(Perfil.ADMIN);
        Vacina vacina = new Vacina();
        vacina.setNome("Teste Antirrábica " + UUID.randomUUID());
        vacina.setEspecieAlvo(Especie.CAO);
        vacinas.saveAndFlush(vacina);
        VacinacaoAnimal aplicacao = new VacinacaoAnimal();
        aplicacao.setAnimal(a);
        aplicacao.setVacina(vacina);
        aplicacao.setDataAplicacao(LocalDate.now());
        vacinacoes.saveAndFlush(aplicacao);
        mvc.perform(delete("/api/animais/{id}", a.getId())
                .header("Authorization", token(admin)))
                .andExpect(status().isConflict());
        assertTrue(animais.existsById(a.getId()));
    }

    @Test
    @Transactional
    void adotanteNaoPodeConsultarSolicitacaoDeTerceiro() throws Exception {
        Usuario dono = usuario(Perfil.ADOTANTE);
        Usuario outro = usuario(Perfil.ADOTANTE);
        Usuario voluntario = usuario(Perfil.VOLUNTARIO);
        Animal animal = animal();
        SolicitacaoAdocao pedido = new SolicitacaoAdocao();
        pedido.setAdotante(dono);
        pedido.setAnimal(animal);
        pedido.setJustificativa("Quero adotar e oferecer um lar seguro.");
        solicitacoes.saveAndFlush(pedido);

        mvc.perform(get("/api/solicitacoes/{id}", pedido.getId())
                .header("Authorization", token(dono)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.animal").value("Pipoca"));
        mvc.perform(get("/api/solicitacoes/{id}", pedido.getId())
                .header("Authorization", token(outro)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios/{id}/solicitacoes", dono.getId())
                .header("Authorization", token(outro)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios/{id}/solicitacoes", dono.getId())
                .header("Authorization", token(voluntario)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(pedido.getId()));
    }

    @Test
    @Transactional
    void consultaDeAdocoesValidaIntervaloDeDatas() throws Exception {
        Usuario admin = usuario(Perfil.ADMIN);
        mvc.perform(get("/api/adocoes?de=2026-12-31&ate=2026-01-01")
                .header("Authorization", token(admin)))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/adocoes?de=2026-01-01")
                .header("Authorization", token(admin)))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/adocoes?de=2026-01-01&ate=2026-12-31")
                .header("Authorization", token(admin)))
                .andExpect(status().isOk());
    }
}

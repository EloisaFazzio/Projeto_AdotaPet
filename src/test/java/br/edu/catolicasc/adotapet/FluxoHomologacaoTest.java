package br.edu.catolicasc.adotapet;

import br.edu.catolicasc.adotapet.model.Usuario;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.UsuarioRepository;
import br.edu.catolicasc.adotapet.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testa as rotas reais em sequência: ONG cadastra abrigo/animal/vacina,
 * adotante solicita, equipe analisa/aprova, adocao e visita são registradas.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FluxoHomologacaoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JwtService jwt;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder encoder;

    private Usuario criarEquipe(Perfil perfil) {
        Usuario u = new Usuario();
        u.setNome("Equipe de Homologação");
        u.setEmail("homologacao-" + UUID.randomUUID() + "@example.invalid");
        u.setSenhaHash(encoder.encode("SenhaLocalTeste123!"));
        u.setPerfil(perfil);
        u.setAtivo(true);
        return usuarios.saveAndFlush(u);
    }

    private String bearer(Usuario u) { return "Bearer " + jwt.gerar(u.getEmail()); }
    private String body(Object value) throws Exception { return mapper.writeValueAsString(value); }
    private JsonNode json(MvcResult resposta) throws Exception {
        return mapper.readTree(resposta.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    void interfacesEApiSaoEntreguesPeloMesmoServidor() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("AdotaPet")));
        mvc.perform(get("/painel.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Gestão AdotaPet")));
        mvc.perform(get("/app.js")).andExpect(status().isOk());
        mvc.perform(get("/painel.js")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("AdotaPet — API REST"));
    }

    @Test
    @Transactional
    void fluxoAdocaoComVacinaEVisitaFuncionaComAutorizacao() throws Exception {
        Usuario admin = criarEquipe(Perfil.ADMIN);
        Usuario voluntario = criarEquipe(Perfil.VOLUNTARIO);
        String tokenAdmin = bearer(admin), tokenVoluntario = bearer(voluntario);

        // RF03 - o administrador cadastra o abrigo.
        long abrigoId = json(mvc.perform(post("/api/abrigos")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("nome","Abrigo Integração",
                        "endereco","Rua do Teste 42","cidade","Joinville",
                        "uf","SC","capacidade",10))))
                .andExpect(status().isCreated()).andReturn()).get("id").asLong();

        // RF04 - voluntário registra um animal. Catálogo público o encontra.
        long animalId = json(mvc.perform(post("/api/animais")
                .header("Authorization", tokenVoluntario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("nome","Lua", "especie","CAO",
                        "sexo","FEMEA", "porte","PEQUENO",
                        "abrigoId",abrigoId,"descricao","Animal acolhido pela ONG."))))
                .andExpect(status().isCreated()).andReturn()).get("id").asLong();
        mvc.perform(get("/api/animais/{id}",animalId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lua"));

        // RF06/07 - vacina, aplicação e histórico válidos para a espécie.
        long vacinaId = json(mvc.perform(post("/api/vacinas")
                .header("Authorization", tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("nome","Vacina Homologação "+UUID.randomUUID(),
                        "especieAlvo","CAO","intervaloDias",365))))
                .andExpect(status().isCreated()).andReturn()).get("id").asLong();
        mvc.perform(post("/api/animais/{id}/vacinacoes",animalId)
                .header("Authorization", tokenVoluntario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("vacinaId",vacinaId,
                        "dataAplicacao",LocalDate.now().minusDays(12).toString(),
                        "veterinario","Dra. Teste"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vacinaId").value(vacinaId));
        mvc.perform(get("/api/animais/{id}/vacinacoes",animalId)
                .header("Authorization", tokenVoluntario))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].animalId").value(animalId));

        // RF01 - autocadastro nunca promove usuário a ADMIN.
        String email = "adotante-" + UUID.randomUUID() + "@example.invalid";
        JsonNode sessao = json(mvc.perform(post("/api/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("nome","Adotante de Teste",
                        "email",email,"senha","SenhaSegura123!","perfil","ADMIN"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.usuario.perfil").value("ADOTANTE"))
                .andReturn());
        String tokenAdotante = "Bearer "+sessao.get("token").asText();
        mvc.perform(get("/api/usuarios").header("Authorization",tokenAdotante))
                .andExpect(status().isForbidden());

        // RF08 e RN03 - solicita, inicia análise e impede acesso de perfis indevidos.
        long pedidoId = json(mvc.perform(post("/api/solicitacoes")
                .header("Authorization",tokenAdotante)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("animalId",animalId,
                        "justificativa","Tenho ambiente seguro e condições de cuidar do animal."))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn()).get("id").asLong();
        mvc.perform(patch("/api/solicitacoes/{id}/analisar",pedidoId)
                .header("Authorization",tokenVoluntario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANALISE"));
        mvc.perform(get("/api/animais/{id}",animalId))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/api/solicitacoes/{id}/aprovar",pedidoId)
                .header("Authorization",tokenVoluntario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("parecer","Aprovado após análise da equipe."))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));

        // RF10 - formalização exige termo e gera animal ADOTADO.
        long adocaoId = json(mvc.perform(post("/api/adocoes")
                .header("Authorization",tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("solicitacaoId",pedidoId,
                        "termoAssinado",true,"observacoes","Termo assinado na ONG."))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.termoAssinado").value(true))
                .andReturn()).get("id").asLong();
        mvc.perform(get("/api/animais/{id}",animalId)
                .header("Authorization",tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ADOTADO"));
        mvc.perform(get("/api/adocoes").header("Authorization",tokenAdotante))
                .andExpect(status().isForbidden());

        // RF11 - voluntário faz visita e o histórico é recuperável.
        mvc.perform(post("/api/adocoes/{id}/visitas",adocaoId)
                .header("Authorization",tokenVoluntario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(Map.of("dataVisita",LocalDate.now().toString(),
                        "observacoes","Animal acolhido com segurança.",
                        "animalBemCuidado",true))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.animalBemCuidado").value(true));
        mvc.perform(get("/api/adocoes/{id}/visitas",adocaoId)
                .header("Authorization",tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].observacoes").value("Animal acolhido com segurança."));

        // RF12 - dados de relatório realmente refletem a adoção registrada.
        mvc.perform(get("/api/relatorios/animais-por-status")
                .header("Authorization",tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ADOTADO").isNumber());
        mvc.perform(get("/api/relatorios/adocoes-por-periodo")
                .param("de",LocalDate.now().minusDays(1).toString())
                .param("ate",LocalDate.now().plusDays(1).toString())
                .header("Authorization",tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber());

        // A solicitação continua consultável para o adotante depois da formalização.
        mvc.perform(get("/api/usuarios/me/solicitacoes")
                .header("Authorization",tokenAdotante))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APROVADA"));
        assertTrue(adocaoId > 0);
    }
}

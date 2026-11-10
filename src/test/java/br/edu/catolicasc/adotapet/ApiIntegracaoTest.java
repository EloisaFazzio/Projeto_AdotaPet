package br.edu.catolicasc.adotapet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegracaoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void catalogoPodeSerConsultadoSemLogin() throws Exception {
        mvc.perform(get("/api/animais")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        mvc.perform(get("/api/abrigos")).andExpect(status().isOk());
    }

    @Test
    void areaAdministrativaExigeAutenticacao() throws Exception {
        mvc.perform(post("/api/abrigos").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroCriaSomenteAdotanteEProtegeGestao() throws Exception {
        String email = "teste-" + UUID.randomUUID() + "@exemplo.com";
        String corpo = mapper.writeValueAsString(Map.of(
                "nome", "Pessoa Teste", "email", email,
                "senha", "SenhaSegura123!", "perfil", "ADMIN"));
        String resposta = mvc.perform(post("/api/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.perfil").value("ADOTANTE"))
                .andExpect(jsonPath("$.usuario.email").value(email))
                .andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(resposta).get("token").asText();
        mvc.perform(post("/api/abrigos").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void dadosDeCadastroInvalidosRetornam400() throws Exception {
        mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"email\":\"invalido\",\"senha\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }
}

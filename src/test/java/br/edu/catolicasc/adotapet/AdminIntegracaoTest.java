package br.edu.catolicasc.adotapet;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.Perfil;
import br.edu.catolicasc.adotapet.repository.*;
import br.edu.catolicasc.adotapet.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminIntegracaoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UsuarioRepository usuarios;
    @Autowired AbrigoRepository abrigos;
    @Autowired PasswordEncoder senhas;
    @Autowired JwtService jwt;

    private Usuario criarUsuario(Perfil perfil) {
        Usuario u = new Usuario();
        u.setNome("Usuário de Integração");
        u.setEmail("integracao-" + UUID.randomUUID() + "@exemplo.com");
        u.setSenhaHash(senhas.encode("SenhaSegura123!"));
        u.setPerfil(perfil);
        u.setAtivo(true);
        return usuarios.saveAndFlush(u);
    }

    private String token(Usuario u) { return "Bearer " + jwt.gerar(u.getEmail()); }
    private String json(Object obj) throws Exception { return mapper.writeValueAsString(obj); }

    @Test
    @Transactional
    void adminCadastraVoluntarioSemExporHashEAdotanteNaoConsegue() throws Exception {
        Usuario admin = criarUsuario(Perfil.ADMIN);
        Usuario adotante = criarUsuario(Perfil.ADOTANTE);
        Map<String, Object> novo = Map.of(
                "nome", "Maria Voluntária",
                "email", "maria-" + UUID.randomUUID() + "@exemplo.com",
                "senha", "SenhaForte123!",
                "perfil", "VOLUNTARIO",
                "cidade", "Jaraguá do Sul");
        String corpo = json(novo);
        mvc.perform(post("/api/usuarios")
                .header("Authorization", token(adotante))
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isForbidden());

        String resposta = mvc.perform(post("/api/usuarios")
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("VOLUNTARIO"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.senhaHash").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resposta).get("id").longValue();
        Usuario criado = usuarios.findById(id).orElseThrow();
        assertTrue(senhas.matches("SenhaForte123!", criado.getSenhaHash()));
        assertNotEquals("SenhaForte123!", criado.getSenhaHash());
        mvc.perform(get("/api/usuarios").header("Authorization", token(adotante)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios/{id}", id).header("Authorization", token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    @Transactional
    void desativacaoMantemHistoricoEProibeAutodesativacao() throws Exception {
        Usuario admin = criarUsuario(Perfil.ADMIN);
        Usuario alvo = criarUsuario(Perfil.ADOTANTE);
        mvc.perform(delete("/api/usuarios/{id}", admin.getId())
                .header("Authorization", token(admin))).andExpect(status().isConflict());
        mvc.perform(delete("/api/usuarios/{id}", alvo.getId())
                .header("Authorization", token(admin))).andExpect(status().isNoContent());
        assertFalse(usuarios.findById(alvo.getId()).orElseThrow().isAtivo());
        mvc.perform(get("/api/auth/me").header("Authorization", token(alvo)))
                .andExpect(status().isUnauthorized());
        mvc.perform(patch("/api/usuarios/{id}/reativar", alvo.getId())
                .header("Authorization", token(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @Transactional
    void apenasVoluntarioAtivoPodeSerVinculadoAoAbrigo() throws Exception {
        Usuario admin = criarUsuario(Perfil.ADMIN);
        Usuario voluntario = criarUsuario(Perfil.VOLUNTARIO);
        Usuario adotante = criarUsuario(Perfil.ADOTANTE);
        String abrigoJson = json(Map.of(
                "nome", "Abrigo Amigo", "endereco", "Rua das Flores, 31",
                "cidade", "Jaraguá do Sul", "uf", "SC", "capacidade", 5));
        String resposta = mvc.perform(post("/api/abrigos")
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON).content(abrigoJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long abrigoId = mapper.readTree(resposta).get("id").asLong();

        mvc.perform(post("/api/abrigos/{id}/voluntarios", abrigoId)
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("usuarioId", adotante.getId()))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/abrigos/{id}/voluntarios", abrigoId)
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("usuarioId", voluntario.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(voluntario.getEmail()));
        mvc.perform(get("/api/abrigos/{id}/voluntarios", abrigoId)
                .header("Authorization", token(adotante))).andExpect(status().isForbidden());
        mvc.perform(delete("/api/abrigos/{id}/voluntarios/{uid}", abrigoId, voluntario.getId())
                .header("Authorization", token(admin))).andExpect(status().isNoContent());
        mvc.perform(get("/api/abrigos/{id}/voluntarios", abrigoId)
                .header("Authorization", token(admin))).andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mvc.perform(delete("/api/abrigos/{id}", abrigoId)
                .header("Authorization", token(admin))).andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void capacidadeDoAbrigoNaoPodeFicarAbaixoDaOcupacao() throws Exception {
        Usuario admin = criarUsuario(Perfil.ADMIN);
        String abrigo = mvc.perform(post("/api/abrigos")
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", "Abrigo Patinhas", "endereco", "Rua Central 123",
                        "cidade", "Joinville", "uf", "SC", "capacidade", 4))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(abrigo).get("id").asLong();
        mvc.perform(post("/api/animais")
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", "Luna", "especie", "GATO", "sexo", "FEMEA",
                        "porte", "PEQUENO", "abrigoId", id, "castrado", false))))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/abrigos/{id}", id)
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", "Abrigo Patinhas", "endereco", "Rua Central 123",
                        "cidade", "Joinville", "uf", "SC", "capacidade", 1))))
                .andExpect(status().isOk());
        // Tentar capacidade inválida (zero) viola Bean Validation antes de persisitir.
        mvc.perform(put("/api/abrigos/{id}", id)
                .header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", "Abrigo Patinhas", "endereco", "Rua Central 123",
                        "cidade", "Joinville", "uf", "SC", "capacidade", 0))))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/abrigos/{id}", id)
                .header("Authorization", token(admin))).andExpect(status().isConflict());
    }
}

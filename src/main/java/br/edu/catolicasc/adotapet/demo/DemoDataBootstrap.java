package br.edu.catolicasc.adotapet.demo;

import br.edu.catolicasc.adotapet.model.*;
import br.edu.catolicasc.adotapet.model.enums.*;
import br.edu.catolicasc.adotapet.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Registros explicitamente fictícios para homologação local; nunca habilite em produção. */
@Configuration
@Profile("dev")
@ConditionalOnProperty(name = "adotapet.demo.seed", havingValue = "true")
public class DemoDataBootstrap {
    @Bean
    ApplicationRunner dadosDemonstracao(AbrigoRepository abrigos, AnimalRepository animais,
            VacinaRepository vacinas, VacinacaoAnimalRepository aplicacoes,
            UsuarioRepository usuarios, SolicitacaoAdocaoRepository pedidos,
            PasswordEncoder encoder, TransactionTemplate tx) {
        return args -> tx.executeWithoutResult(status -> {
            // Não modifica registros reais. Uma segunda execução é idempotente.
            if (abrigos.findAll().stream().anyMatch(a -> a.getNome().startsWith("DEMO — ") || a.getNome().equals("Lar Amigo Jaraguá"))) return;
            String[][] locais = {
                {"DEMO — Lar Amigo Jaraguá", "Jaraguá do Sul"},
                {"DEMO — Patas de Schroeder", "Schroeder"},
                {"DEMO — Rede Animal Guaramirim", "Guaramirim"},
                {"DEMO — Cuidado em Corupá", "Corupá"}
            };
            Abrigo[] rede = new Abrigo[locais.length];
            for (int i = 0; i < locais.length; i++) {
                Abrigo a = new Abrigo();
                a.setNome(locais[i][0]); a.setEndereco("Endereço fictício — somente demonstração");
                a.setCidade(locais[i][1]); a.setUf("SC"); a.setCapacidade(30);
                rede[i] = abrigos.save(a);
            }
            String[][] pets = {
                {"Luna","CAO","FEMEA","PEQUENO","0","SRD","Carinhosa e tranquila, gosta de companhia."},
                {"Thor","CAO","MACHO","GRANDE","1","SRD","Brincalhão e cheio de energia."},
                {"Mimi","GATO","FEMEA","PEQUENO","2","SRD","Curiosa e acostumada a ambientes internos."},
                {"Bento","GATO","MACHO","MEDIO","3","SRD","Gosta de carinho e de lugares confortáveis."},
                {"Nina","CAO","FEMEA","MEDIO","1","SRD","Adora caminhadas e brincadeiras."},
                {"Tobias","CAO","MACHO","PEQUENO","2","SRD","Companheiro e sociável."},
                {"Amora","GATO","FEMEA","PEQUENO","3","SRD","Gatinha dócil e curiosa."},
                {"Pipoca","OUTRA","FEMEA","PEQUENO","0","Coelho","Coelhinha calma, precisa de ambiente seguro."},
                {"Mel","CAO","FEMEA","GRANDE","2","SRD","Muito afetuosa e ativa."},
                {"Batmiau","GATO","MACHO","MEDIO","0","SRD","Sociável e atento ao ambiente."},
                {"Fred","CAO","MACHO","MEDIO","3","SRD","Amigável e curioso."},
                {"Sol","OUTRA","MACHO","PEQUENO","1","Porquinho-da-índia","Pequeno e delicado, requer cuidados específicos."},
                {"Bela","CAO","FEMEA","PEQUENO","0","SRD","Gosta de colo e carinho."},
                {"Chico","GATO","MACHO","PEQUENO","1","SRD","Brinca bastante e gosta de explorar."},
                {"Dora","CAO","FEMEA","MEDIO","2","SRD","Esperta e muito companheira."},
                {"Oliver","GATO","MACHO","PEQUENO","3","SRD","Tranquilo, procura um lar acolhedor."}
            };
            Animal[] lista = new Animal[pets.length];
            for (int i = 0; i < pets.length; i++) {
                String[] p = pets[i]; Animal a = new Animal();
                a.setNome(p[0]); a.setEspecie(Especie.valueOf(p[1]));
                a.setSexo(Sexo.valueOf(p[2])); a.setPorte(Porte.valueOf(p[3]));
                a.setAbrigo(rede[Integer.parseInt(p[4])]); a.setRaca(p[5]);
                a.setDescricao(p[6]);
                a.setDataNascimento(LocalDate.now().minusMonths(10 + i * 3L));
                a.setCastrado(i % 3 == 0);
                lista[i] = animais.save(a);
            }
            String[][] nomesVacinas = {{"Antirrábica canina","CAO"}, {"V8 canina","CAO"},
                {"Antirrábica felina","GATO"}, {"V4 felina","GATO"}};
            Vacina[] catalogo = new Vacina[nomesVacinas.length];
            for (int i = 0; i < nomesVacinas.length; i++) {
                Vacina vacina = new Vacina(); vacina.setNome(nomesVacinas[i][0]);
                vacina.setEspecieAlvo(Especie.valueOf(nomesVacinas[i][1])); vacina.setIntervaloDias(365);
                catalogo[i] = vacinas.save(vacina);
            }
            for (int i = 0; i < 8; i++) {
                Animal a = lista[i];
                Vacina v = a.getEspecie() == Especie.CAO ? catalogo[i % 2] :
                           a.getEspecie() == Especie.GATO ? catalogo[2 + (i % 2)] : null;
                if (v == null) continue;
                VacinacaoAnimal dose = new VacinacaoAnimal(); dose.setAnimal(a); dose.setVacina(v);
                dose.setDataAplicacao(LocalDate.now().minusDays(35 + i * 12));
                dose.setProximaDose(dose.getDataAplicacao().plusDays(365));
                dose.setVeterinario("Profissional fictício — demonstração");
                aplicacoes.save(dose);
            }
            // Tutores fictícios não recebem senha pública ou compartilhada.
            for (int i = 0; i < 4; i++) {
                Usuario tutor = new Usuario();
                tutor.setNome("Tutor demonstrativo " + (i + 1));
                tutor.setEmail("tutor-demo-" + (i+1) + "@example.invalid");
                tutor.setSenhaHash(encoder.encode(UUID.randomUUID().toString()));
                tutor.setPerfil(Perfil.ADOTANTE); tutor.setAtivo(true);
                tutor.setCidade(locais[i][1]); usuarios.save(tutor);
                SolicitacaoAdocao pedido = new SolicitacaoAdocao();
                pedido.setAdotante(tutor); pedido.setAnimal(lista[i]);
                pedido.setJustificativa("Tenho interesse em conhecer o animal e seguir todas as etapas da adoção responsável.");
                pedidos.save(pedido);
            }
        });
    }
}

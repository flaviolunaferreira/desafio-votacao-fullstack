package full.stack.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Pauta pauta;
    private SessaoVotacao sessao;
    private VotoRequestDTO requestDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        votoRepository.deleteAll();
        sessaoVotacaoRepository.deleteAll();
        pautaRepository.deleteAll();

        now = LocalDateTime.now();

        pauta = new Pauta();
        pauta.setTitulo("Test Pauta");
        pauta.setDescricao("Descrição da pauta");
        pauta = pautaRepository.save(pauta);

        sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setDataAbertura(now);
        sessao.setDataFechamento(now.plusMinutes(10));
        sessao = sessaoVotacaoRepository.save(sessao);

        requestDTO = new VotoRequestDTO();
        requestDTO.setSessaoId(sessao.getId());
        requestDTO.setCpf("11144477735");
        requestDTO.setVoto(true);
    }

    @Test
    void votar_pautaValidaSessaoAbertaCpfValidoNaoVotado_retorna201() throws Exception {
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.associadoCpf").value("11144477735"))
                .andExpect(jsonPath("$.voto").value(true));
    }

    @Test
    void votar_pautaIdNulo_retorna400() throws Exception {
        requestDTO.setSessaoId(null);
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pautaId: O ID da pauta é obrigatório"));
    }

    @Test
    void votar_cpfNulo_retorna400() throws Exception {
        requestDTO.setCpf(null);
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("cpf: O CPF do associado é obrigatório"));
    }

    @Test
    void votar_cpfInvalido_retorna400() throws Exception {
        requestDTO.setCpf("123");
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF inválido: 123"));
    }

    @Test
    void votar_votoNulo_retorna400() throws Exception {
        requestDTO.setCpf("11144477735"); // Restaurar CPF válido
        requestDTO.setVoto(null);
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("voto: O voto (Sim/Não) é obrigatório"));
    }

    @Test
    void votar_pautaNaoExistente_retorna404() throws Exception {
        requestDTO.setSessaoId(999L);
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }

    @Test
    void votar_sessaoNaoExistente_retorna400() throws Exception {
        sessaoVotacaoRepository.deleteAll();
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nenhuma sessão de votação aberta para a pauta: " + pauta.getId()));
    }

    @Test
    void votar_sessaoFechada_retorna400() throws Exception {
        sessao.setDataFechamento(now.minusMinutes(1));
        sessaoVotacaoRepository.save(sessao);

        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sessão de votação encerrada para a pauta: " + pauta.getId()));
    }

    @Test
    void votar_usuarioJaVotou_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Associado com CPF 11144477735 já votou na pauta: " + pauta.getId()));
    }

    @Test
    void buscarVoto_votoExistente_retorna200() throws Exception {
        VotoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                VotoResponseDTO.class
        );

        mockMvc.perform(get("/api/v1/votos/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.associadoCpf").value("11144477735"))
                .andExpect(jsonPath("$.voto").value(true));
    }

    @Test
    void buscarVoto_votoNaoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/votos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Voto não encontrado: 999"));
    }

    @Test
    void listarVotos_comPautaId_retorna200() throws Exception {
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/votos?pautaId=" + pauta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$[0].associadoCpf").value("11144477735"))
                .andExpect(jsonPath("$[0].voto").value(true));
    }

    @Test
    void listarVotos_semPautaId_retorna200() throws Exception {
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/votos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$[0].associadoCpf").value("11144477735"))
                .andExpect(jsonPath("$[0].voto").value(true));
    }

    @Test
    void listarVotos_pautaNaoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/votos?pautaId=999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }

    @Test
    void atualizarVoto_votoExistenteSessaoAberta_retorna200() throws Exception {
        VotoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                VotoResponseDTO.class
        );

        requestDTO.setCpf("98765432100");
        requestDTO.setVoto(false);

        mockMvc.perform(put("/api/v1/votos/" + response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.associadoCpf").value("98765432100"))
                .andExpect(jsonPath("$.voto").value(false));
    }

    @Test
    void atualizarVoto_votoNaoExistente_retorna404() throws Exception {
        mockMvc.perform(put("/api/v1/votos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Voto não encontrado: 999"));
    }

    @Test
    void atualizarVoto_sessaoFechada_retorna400() throws Exception {
        VotoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                VotoResponseDTO.class
        );

        sessao.setDataFechamento(now.minusMinutes(1));
        sessaoVotacaoRepository.save(sessao);

        mockMvc.perform(put("/api/v1/votos/" + response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sessão de votação encerrada para a pauta: " + pauta.getId()));
    }

    @Test
    void deletarVoto_votoExistenteSessaoAberta_retorna204() throws Exception {
        VotoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                VotoResponseDTO.class
        );

        mockMvc.perform(delete("/api/v1/votos/" + response.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarVoto_votoNaoExistente_retorna404() throws Exception {
        mockMvc.perform(delete("/api/v1/votos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Voto não encontrado: 999"));
    }

    @Test
    void deletarVoto_sessaoFechada_retorna400() throws Exception {
        VotoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                VotoResponseDTO.class
        );

        sessao.setDataFechamento(now.minusMinutes(1));
        sessaoVotacaoRepository.save(sessao);

        mockMvc.perform(delete("/api/v1/votos/" + response.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível deletar voto após o encerramento da sessão"));
    }

    @Test
    void obterResultado_pautaExistente_retorna200() throws Exception {
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        requestDTO.setCpf("98765432100");
        requestDTO.setVoto(false);
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/votos/resultado/" + pauta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.titulo").value("Test Pauta"))
                .andExpect(jsonPath("$.votosSim").value(1))
                .andExpect(jsonPath("$.votosNao").value(1));
    }

    @Test
    void obterResultado_pautaNaoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/votos/resultado/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }
}
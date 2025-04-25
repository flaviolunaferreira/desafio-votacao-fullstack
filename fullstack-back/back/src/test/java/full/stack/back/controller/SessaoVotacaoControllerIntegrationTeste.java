package full.stack.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.dto.SessaoVotacaoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessaoVotacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Pauta pauta;
    private SessaoVotacaoRequestDTO requestDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        sessaoVotacaoRepository.deleteAll();
        pautaRepository.deleteAll();

        now = LocalDateTime.now();
        pauta = new Pauta();
        pauta.setTitulo("Test Pauta");
        pauta.setDescricao("Descrição da pauta de teste");
        pauta = pautaRepository.save(pauta);

        requestDTO = new SessaoVotacaoRequestDTO();
        requestDTO.setPautaId(pauta.getId());
        requestDTO.setDuracao(10);
    }

    @Test
    void abrirSessao_pautaExistenteSemSessao_duracaoInformada_retorna201() throws Exception {
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.dataFechamento").exists());
    }

    @Test
    void abrirSessao_pautaExistenteSemSessao_duracaoNula_retorna201() throws Exception {
        requestDTO.setDuracao(null);
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.dataFechamento").exists());
    }

    @Test
    void abrirSessao_pautaNaoExistente_retorna404() throws Exception {
        requestDTO.setPautaId(999L);
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }

    @Test
    void abrirSessao_sessaoJaAberta_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe uma sessão de votação aberta para a pauta: " + pauta.getId()));
    }

    @Test
    void abrirSessao_pautaIdNulo_retorna400() throws Exception {
        requestDTO.setPautaId(null);
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pautaId: O ID da pauta é obrigatório"));
    }

    @Test
    void abrirSessao_duracaoNaoPositiva_retorna400() throws Exception {
        requestDTO.setDuracao(0);
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("duracao: A duração deve ser um número positivo"));
    }

    @Test
    void buscarSessao_sessaoExistente_retorna200() throws Exception {
        SessaoVotacaoResponseDTO response = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                SessaoVotacaoResponseDTO.class
        );

        mockMvc.perform(get("/api/v1/sessoes/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.dataFechamento").exists());
    }

    @Test
    void buscarSessao_sessaoNaoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/sessoes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sessão não encontrada: 999"));
    }
}
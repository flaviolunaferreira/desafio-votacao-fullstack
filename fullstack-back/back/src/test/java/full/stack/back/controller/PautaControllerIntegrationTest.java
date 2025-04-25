package full.stack.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PautaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PautaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pautaRepository.deleteAll();

        requestDTO = new PautaRequestDTO();
        requestDTO.setTitulo("Test Pauta");
        requestDTO.setDescricao("Descrição da pauta");
    }

    @Test
    void criarPauta_valida_retorna201() throws Exception {
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Test Pauta"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));
    }

    @Test
    void criarPauta_tituloNulo_retorna400() throws Exception {
        requestDTO.setTitulo(null);
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("titulo: O título da pauta é obrigatório"));
    }

    @Test
    void buscarPauta_pautaExistente_retorna200() throws Exception {
        Pauta pauta = new Pauta("Test Pauta", "Descrição da pauta");
        pauta = pautaRepository.save(pauta);

        mockMvc.perform(get("/api/v1/pautas/" + pauta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pauta.getId()))
                .andExpect(jsonPath("$.titulo").value("Test Pauta"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));
    }

    @Test
    void buscarPauta_pautaNaoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }

    @Test
    void listarPautas_retorna200() throws Exception {
        Pauta pauta = new Pauta("Test Pauta", "Descrição da pauta");
        pautaRepository.save(pauta);

        mockMvc.perform(get("/api/v1/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titulo").value("Test Pauta"))
                .andExpect(jsonPath("$[0].descricao").value("Descrição da pauta"));
    }

    @Test
    void atualizarPauta_pautaExistente_retorna200() throws Exception {
        Pauta pauta = new Pauta("Test Pauta", "Descrição da pauta");
        pauta = pautaRepository.save(pauta);

        requestDTO.setTitulo("Pauta Atualizada");
        mockMvc.perform(put("/api/v1/pautas/" + pauta.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Pauta Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));
    }

    @Test
    void atualizarPauta_pautaNaoExistente_retorna404() throws Exception {
        mockMvc.perform(put("/api/v1/pautas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada: 999"));
    }
}
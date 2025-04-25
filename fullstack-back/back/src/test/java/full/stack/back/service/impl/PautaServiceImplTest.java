package full.stack.back.service;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.dto.PautaResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.service.impl.PautaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceImplTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaServiceImpl pautaService;

    private Pauta pauta;
    private PautaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Test Pauta");
        pauta.setDescricao("Descrição da pauta");

        requestDTO = new PautaRequestDTO();
        requestDTO.setTitulo("Test Pauta");
        requestDTO.setDescricao("Descrição da pauta");
    }

    @Test
    void criarPauta_valida_retornaPautaResponseDTO() {
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponseDTO response = pautaService.criarPauta(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Pauta", response.getTitulo());
        assertEquals("Descrição da pauta", response.getDescricao());
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void buscarPauta_pautaExistente_retornaPautaResponseDTO() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        PautaResponseDTO response = pautaService.buscarPauta(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Pauta", response.getTitulo());
        assertEquals("Descrição da pauta", response.getDescricao());
        verify(pautaRepository).findById(1L);
    }

    @Test
    void buscarPauta_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pautaService.buscarPauta(1L));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
    }

    @Test
    void listarPautas_retornaListaDePautas() {
        when(pautaRepository.findAll()).thenReturn(Arrays.asList(pauta));

        List<PautaResponseDTO> response = pautaService.listarPautas();

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("Test Pauta", response.get(0).getTitulo());
        assertEquals("Descrição da pauta", response.get(0).getDescricao());
        verify(pautaRepository).findAll();
    }

    @Test
    void atualizarPauta_pautaExistente_retornaPautaResponseDTO() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponseDTO response = pautaService.atualizarPauta(1L, requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Pauta", response.getTitulo());
        assertEquals("Descrição da pauta", response.getDescricao());
        verify(pautaRepository).findById(1L);
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void atualizarPauta_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pautaService.atualizarPauta(1L, requestDTO));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verifyNoMoreInteractions(pautaRepository);
    }
}
package full.stack.back.service.impl;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.dto.SessaoVotacaoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.exception.BusinessException;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.service.impl.SessaoVotacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceImplTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoVotacaoServiceImpl sessaoVotacaoService;

    private Pauta pauta;
    private SessaoVotacao sessao;
    private SessaoVotacaoRequestDTO requestDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Test Pauta");

        sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPauta(pauta);
        sessao.setDataAbertura(now);
        sessao.setDataFechamento(now.plusMinutes(10));

        requestDTO = new SessaoVotacaoRequestDTO();
        requestDTO.setPautaId(1L);
        requestDTO.setDuracao(10);
    }

    // Testes para abrirSessao
    @Test
    void abrirSessao_pautaExistenteSemSessao_duracaoInformada_retornaSessao() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findOpenByPautaId(1L)).thenReturn(null);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoVotacaoResponseDTO response = sessaoVotacaoService.abrirSessao(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals(now.plusMinutes(10), response.getDataFechamento());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findOpenByPautaId(1L);
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    void abrirSessao_pautaExistenteSemSessao_duracaoNula_retornaSessaoComDefault() {
        requestDTO.setDuracao(null);
        sessao.setDataFechamento(now.plusMinutes(1));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findOpenByPautaId(1L)).thenReturn(null);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoVotacaoResponseDTO response = sessaoVotacaoService.abrirSessao(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals(now.plusMinutes(1), response.getDataFechamento());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findOpenByPautaId(1L);
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    void abrirSessao_pautaExistenteSemSessao_duracaoZero_retornaSessaoComDefault() {
        requestDTO.setDuracao(0);
        sessao.setDataFechamento(now.plusMinutes(1));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findOpenByPautaId(1L)).thenReturn(null);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoVotacaoResponseDTO response = sessaoVotacaoService.abrirSessao(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals(now.plusMinutes(1), response.getDataFechamento());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findOpenByPautaId(1L);
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    void abrirSessao_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> sessaoVotacaoService.abrirSessao(requestDTO));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verifyNoInteractions(sessaoVotacaoRepository);
    }

    @Test
    void abrirSessao_sessaoJaAberta_lancaBusinessException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findOpenByPautaId(1L)).thenReturn(new SessaoVotacao());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sessaoVotacaoService.abrirSessao(requestDTO));

        assertEquals("Já existe uma sessão de votação aberta para a pauta: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findOpenByPautaId(1L);
        verifyNoMoreInteractions(sessaoVotacaoRepository);
    }

    // Testes para buscarSessao
    @Test
    void buscarSessao_sessaoExistente_retornaSessao() {
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));

        SessaoVotacaoResponseDTO response = sessaoVotacaoService.buscarSessao(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals(now, response.getDataAbertura());
        assertEquals(now.plusMinutes(10), response.getDataFechamento());
        verify(sessaoVotacaoRepository).findById(1L);
    }

    @Test
    void buscarSessao_sessaoNaoExistente_lancaResourceNotFoundException() {
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> sessaoVotacaoService.buscarSessao(1L));

        assertEquals("Sessão não encontrada: 1", exception.getMessage());
        verify(sessaoVotacaoRepository).findById(1L);
    }
}
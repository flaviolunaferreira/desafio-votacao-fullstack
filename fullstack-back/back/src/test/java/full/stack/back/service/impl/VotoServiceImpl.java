package full.stack.back.service.impl;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.entity.Voto;
import full.stack.back.exception.BusinessException;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceImplTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private VotoServiceImpl votoService;

    private Pauta pauta;
    private SessaoVotacao sessao;
    private Voto voto;
    private VotoRequestDTO requestDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Test Pauta");
        pauta.setDescricao("Descrição da pauta");

        sessao = new SessaoVotacao();
        sessao.setId(1L);
        sessao.setPauta(pauta);
        sessao.setDataAbertura(now.minusMinutes(10));
        sessao.setDataFechamento(now.plusMinutes(10));

        voto = new Voto();
        voto.setId(1L);
        voto.setPauta(pauta);
        voto.setAssociadoCpf("11144477735");
        voto.setVoto(true);
        voto.setDataVoto(now);

        requestDTO = new VotoRequestDTO();
        requestDTO.setPautaId(1L);
        requestDTO.setCpf("11144477735");
        requestDTO.setVoto(true);
    }

    @Test
    void votar_pautaValidaSessaoAbertaCpfValidoNaoVotado_retornaVoto() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndAssociadoCpf(1L, "11144477735")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(voto);

        VotoResponseDTO response = votoService.votar(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals("11144477735", response.getAssociadoCpf());
        assertTrue(response.getVoto());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verify(votoRepository).existsByPautaIdAndAssociadoCpf(1L, "11144477735");
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void votar_cpfInvalido_lancaBusinessException() {
        requestDTO.setCpf("123");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.votar(requestDTO));

        assertEquals("CPF inválido: 123", exception.getMessage());
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository, votoRepository);
    }

    @Test
    void votar_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.votar(requestDTO));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verifyNoInteractions(sessaoVotacaoRepository, votoRepository);
    }

    @Test
    void votar_sessaoNaoExistente_lancaBusinessException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.votar(requestDTO));

        assertEquals("Nenhuma sessão de votação aberta para a pauta: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verifyNoInteractions(votoRepository);
    }

    @Test
    void votar_sessaoFechada_lancaBusinessException() {
        sessao.setDataFechamento(now.minusMinutes(1));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.votar(requestDTO));

        assertEquals("Sessão de votação encerrada para a pauta: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verifyNoInteractions(votoRepository);
    }

    @Test
    void votar_usuarioJaVotou_lancaBusinessException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndAssociadoCpf(1L, "11144477735")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.votar(requestDTO));

        assertEquals("Associado com CPF 11144477735 já votou na pauta: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verify(votoRepository).existsByPautaIdAndAssociadoCpf(1L, "11144477735");
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    void buscarVoto_votoExistente_retornaVoto() {
        when(votoRepository.findById(1L)).thenReturn(Optional.of(voto));

        VotoResponseDTO response = votoService.buscarVoto(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals("11144477735", response.getAssociadoCpf());
        assertTrue(response.getVoto());
        verify(votoRepository).findById(1L);
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    void buscarVoto_votoNaoExistente_lancaResourceNotFoundException() {
        when(votoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.buscarVoto(1L));

        assertEquals("Voto não encontrado: 1", exception.getMessage());
        verify(votoRepository).findById(1L);
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    void listarVotos_comPautaId_retornaVotosDaPauta() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.findByPautaId(1L)).thenReturn(Collections.singletonList(voto));

        List<VotoResponseDTO> response = votoService.listarVotos(1L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(1L, response.get(0).getPautaId());
        assertEquals("11144477735", response.get(0).getAssociadoCpf());
        assertTrue(response.get(0).getVoto());
        verify(pautaRepository).findById(1L);
        verify(votoRepository).findByPautaId(1L);
        verifyNoInteractions(sessaoVotacaoRepository);
    }

    @Test
    void listarVotos_semPautaId_retornaTodosVotos() {
        when(votoRepository.findAll()).thenReturn(Collections.singletonList(voto));

        List<VotoResponseDTO> response = votoService.listarVotos(null);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(1L, response.get(0).getPautaId());
        assertEquals("11144477735", response.get(0).getAssociadoCpf());
        assertTrue(response.get(0).getVoto());
        verify(votoRepository).findAll();
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    void listarVotos_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.listarVotos(1L));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verifyNoInteractions(votoRepository, sessaoVotacaoRepository);
    }

    @Test
    void atualizarVoto_votoExistenteSessaoAberta_retornaVotoAtualizado() {
        requestDTO.setCpf("98765432100");
        requestDTO.setVoto(false);
        when(votoRepository.findById(1L)).thenReturn(Optional.of(voto));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);
        when(votoRepository.save(any(Voto.class))).thenReturn(voto);

        VotoResponseDTO response = votoService.atualizarVoto(1L, requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals("98765432100", response.getAssociadoCpf());
        assertFalse(response.getVoto());
        verify(votoRepository).findById(1L);
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void atualizarVoto_votoNaoExistente_lancaResourceNotFoundException() {
        when(votoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.atualizarVoto(1L, requestDTO));

        assertEquals("Voto não encontrado: 1", exception.getMessage());
        verify(votoRepository).findById(1L);
        verifyNoInteractions(pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    void atualizarVoto_cpfInvalido_lancaBusinessException() {
        requestDTO.setCpf("123");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.atualizarVoto(1L, requestDTO));

        assertEquals("CPF inválido: 123", exception.getMessage());
        verifyNoInteractions(votoRepository, pautaRepository, sessaoVotacaoRepository);
    }

    @Test
    void atualizarVoto_sessaoFechada_lancaBusinessException() {
        sessao.setDataFechamento(now.minusMinutes(1));
        when(votoRepository.findById(1L)).thenReturn(Optional.of(voto));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.atualizarVoto(1L, requestDTO));

        assertEquals("Sessão de votação encerrada para a pauta: 1", exception.getMessage());
        verify(votoRepository).findById(1L);
        verify(pautaRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    void deletarVoto_votoExistenteSessaoAberta_deletaVoto() {
        when(votoRepository.findById(1L)).thenReturn(Optional.of(voto));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);

        votoService.deletarVoto(1L);

        verify(votoRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verify(votoRepository).deleteById(1L);
    }

    @Test
    void deletarVoto_votoNaoExistente_lancaResourceNotFoundException() {
        when(votoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.deletarVoto(1L));

        assertEquals("Voto não encontrado: 1", exception.getMessage());
        verify(votoRepository).findById(1L);
        verifyNoInteractions(sessaoVotacaoRepository, pautaRepository);
    }

    @Test
    void deletarVoto_sessaoFechada_lancaBusinessException() {
        sessao.setDataFechamento(now.minusMinutes(1));
        when(votoRepository.findById(1L)).thenReturn(Optional.of(voto));
        when(sessaoVotacaoRepository.findByPautaId(1L)).thenReturn(sessao);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> votoService.deletarVoto(1L));

        assertEquals("Não é possível deletar voto após o encerramento da sessão", exception.getMessage());
        verify(votoRepository).findById(1L);
        verify(sessaoVotacaoRepository).findByPautaId(1L);
        verifyNoMoreInteractions(votoRepository);
    }

    @Test
    void obterResultado_pautaExistente_retornaResultado() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countVotosSim(1L)).thenReturn(5L);
        when(votoRepository.countVotosNao(1L)).thenReturn(3L);

        ResultadoResponseDTO response = votoService.obterResultado(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPautaId());
        assertEquals("Test Pauta", response.getTitulo());
        assertEquals(5L, response.getVotosSim());
        assertEquals(3L, response.getVotosNao());
        verify(pautaRepository).findById(1L);
        verify(votoRepository).countVotosSim(1L);
        verify(votoRepository).countVotosNao(1L);
        verifyNoInteractions(sessaoVotacaoRepository);
    }

    @Test
    void obterResultado_pautaNaoExistente_lancaResourceNotFoundException() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> votoService.obterResultado(1L));

        assertEquals("Pauta não encontrada: 1", exception.getMessage());
        verify(pautaRepository).findById(1L);
        verifyNoInteractions(votoRepository, sessaoVotacaoRepository);
    }
}
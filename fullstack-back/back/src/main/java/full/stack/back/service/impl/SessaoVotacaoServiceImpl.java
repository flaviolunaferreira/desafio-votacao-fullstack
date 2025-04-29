package full.stack.back.service.impl;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.dto.SessaoVotacaoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.exception.BusinessException;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.service.SessaoVotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Validated
public class SessaoVotacaoServiceImpl implements SessaoVotacaoService {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;


    @Override
    public SessaoVotacaoResponseDTO abrirSessao(SessaoVotacaoRequestDTO requestDTO) {
        log.info("Iniciando abertura de sessão para pautaId: {}", requestDTO.getPautaId());

        Pauta pauta = pautaRepository.findById(requestDTO.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + requestDTO.getPautaId()));

        // Verificar se existe uma sessão aberta para a pauta
        SessaoVotacao sessaoAberta = sessaoVotacaoRepository.findOpenByPautaId(requestDTO.getPautaId());
        if (sessaoAberta != null) {
            log.warn("Pauta {} já possui uma sessão aberta com ID: {}", requestDTO.getPautaId(), sessaoAberta.getId());
            throw new BusinessException("Já existe uma sessão de votação aberta para a pauta: " + requestDTO.getPautaId());
        }

        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFechamento = requestDTO.getDuracao() != null && requestDTO.getDuracao() > 0
                ? dataAbertura.plusMinutes(requestDTO.getDuracao())
                : dataAbertura.plusMinutes(1);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setDataAbertura(dataAbertura);
        sessao.setDataFechamento(dataFechamento);

        log.info("Salvando sessão para pautaId: {}", requestDTO.getPautaId());
        try {
            sessao = sessaoVotacaoRepository.save(sessao);
            log.info("Sessão salva com sucesso: {}", sessao.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar sessão para pautaId: {}", requestDTO.getPautaId(), e);
            throw new RuntimeException("Erro ao salvar a sessão: " + e.getMessage(), e);
        }

        log.info("Convertendo sessão para DTO: {}", sessao.getId());
        SessaoVotacaoResponseDTO responseDTO = toResponseDTO(sessao);
        log.info("Sessão convertida para DTO com sucesso");
        return responseDTO;
    }

    @Override
    public SessaoVotacaoResponseDTO buscarSessao(Long id) {
        SessaoVotacao sessao = sessaoVotacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada: " + id));
        return toResponseDTO(sessao);
    }

    @Override
    public List<SessaoVotacaoResponseDTO> listarSessoes() {
        List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll();
        return sessoes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SessaoVotacaoResponseDTO atualizarSessao(Long id, SessaoVotacaoRequestDTO dto) {
        SessaoVotacao sessao = sessaoVotacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada: " + id));

        Pauta pauta = pautaRepository.findById(dto.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + dto.getPautaId()));

        sessao.setPauta(pauta);
        sessao.setDataFechamento(calculateDataFechamento(dto.getDuracao()));
        sessao = sessaoVotacaoRepository.save(sessao);
        return toResponseDTO(sessao);
    }

    @Override
    public void deletarSessao(Long id) {
        if (!sessaoVotacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão não encontrada: " + id);
        }
        sessaoVotacaoRepository.deleteById(id);
    }

    private LocalDateTime calculateDataFechamento(Integer duracao) {
        // Define duração padrão de 1 minuto se não especificada
        int minutos = (duracao != null && duracao > 0) ? duracao : 1;
        return LocalDateTime.now().plusMinutes(minutos);
    }

    private SessaoVotacaoResponseDTO toResponseDTO(SessaoVotacao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("Sessão não pode ser nula");
        }
        SessaoVotacaoResponseDTO dto = new SessaoVotacaoResponseDTO();
        dto.setId(sessao.getId());
        dto.setPautaId(sessao.getPauta() != null ? sessao.getPauta().getId() : null);
        dto.setDataAbertura(sessao.getDataAbertura());
        dto.setDataFechamento(sessao.getDataFechamento());
        return dto;
    }
}
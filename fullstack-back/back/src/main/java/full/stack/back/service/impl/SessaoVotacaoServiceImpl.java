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

@Service
@RequiredArgsConstructor
@Validated
public class SessaoVotacaoServiceImpl implements SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    @Override
    public SessaoVotacaoResponseDTO abrirSessao(SessaoVotacaoRequestDTO requestDTO) {
        Pauta pauta = pautaRepository.findById(requestDTO.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + requestDTO.getPautaId()));

        if (sessaoVotacaoRepository.findByPautaId(requestDTO.getPautaId()) != null) {
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

        sessao = sessaoVotacaoRepository.save(sessao);
        return toResponseDTO(sessao);
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
        SessaoVotacaoResponseDTO dto = new SessaoVotacaoResponseDTO();
        dto.setId(sessao.getId());
        dto.setPautaId(sessao.getPauta().getId());
        dto.setDataAbertura(sessao.getDataAbertura());
        dto.setDataFechamento(sessao.getDataFechamento());
        return dto;
    }
}
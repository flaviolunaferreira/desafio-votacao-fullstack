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
import full.stack.back.service.VotoService;
import full.stack.back.util.CpfValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class VotoServiceImpl implements VotoService {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Override
    public VotoResponseDTO votar(VotoRequestDTO votoDTO) {
        // Validar CPF
        if (!CpfValidation.isValid(votoDTO.getCpf())) {
            throw new BusinessException("CPF inválido: " + votoDTO.getCpf());
        }

        // Verificar pauta
        Pauta pauta = pautaRepository.findById(votoDTO.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + votoDTO.getPautaId()));

        // Verificar sessão de votação
        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(votoDTO.getPautaId());
        if (sessao == null) {
            throw new BusinessException("Nenhuma sessão de votação aberta para a pauta: " + votoDTO.getPautaId());
        }

        // Verificar se a sessão está aberta
        LocalDateTime now = LocalDateTime.now();
        if (sessao.getDataFechamento() != null && now.isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Sessão de votação encerrada para a pauta: " + votoDTO.getPautaId());
        }

        // Verificar se o associado já votou
        if (votoRepository.existsByPautaIdAndAssociadoCpf(votoDTO.getPautaId(), votoDTO.getCpf())) {
            throw new BusinessException("Associado com CPF " + votoDTO.getCpf() + " já votou na pauta: " + votoDTO.getPautaId());
        }

        // Registrar voto
        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setAssociadoCpf(votoDTO.getCpf());
        voto.setVoto(votoDTO.getVoto());
        voto.setDataVoto(now);

        Voto savedVoto = votoRepository.save(voto);
        return new VotoResponseDTO(savedVoto.getId(), savedVoto.getPauta().getId(),
                savedVoto.getAssociadoCpf(), savedVoto.getVoto());
    }

    @Override
    public VotoResponseDTO buscarVoto(Long id) {
        Voto voto = votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado: " + id));
        return toResponseDTO(voto);
    }

    @Override
    public List<VotoResponseDTO> listarVotos(Long pautaId) {
        List<Voto> votos;
        if (pautaId != null) {
            pautaRepository.findById(pautaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + pautaId));
            votos = votoRepository.findByPautaId(pautaId);
        } else {
            votos = votoRepository.findAll();
        }
        return votos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VotoResponseDTO atualizarVoto(Long id, VotoRequestDTO votoDTO) {
        // Validar CPF
        if (!CpfValidation.isValid(votoDTO.getCpf())) {
            throw new BusinessException("CPF inválido: " + votoDTO.getCpf());
        }

        // Verificar voto
        Voto voto = votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado: " + id));

        // Verificar pauta
        Pauta pauta = pautaRepository.findById(votoDTO.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + votoDTO.getPautaId()));

        // Verificar sessão de votação
        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(votoDTO.getPautaId());
        if (sessao == null) {
            throw new BusinessException("Nenhuma sessão de votação aberta para a pauta: " + votoDTO.getPautaId());
        }

        // Verificar se a sessão está aberta
        LocalDateTime now = LocalDateTime.now();
        if (sessao.getDataFechamento() != null && now.isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Sessão de votação encerrada para a pauta: " + votoDTO.getPautaId());
        }

        // Atualizar voto
        voto.setPauta(pauta);
        voto.setAssociadoCpf(votoDTO.getCpf());
        voto.setVoto(votoDTO.getVoto());
        voto.setDataVoto(now);

        Voto updatedVoto = votoRepository.save(voto);
        return new VotoResponseDTO(updatedVoto.getId(), updatedVoto.getPauta().getId(),
                updatedVoto.getAssociadoCpf(), updatedVoto.getVoto());
    }

    @Override
    public void deletarVoto(Long id) {
        Voto voto = votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado: " + id));

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(voto.getPauta().getId());
        if (sessao != null && LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Não é possível deletar voto após o encerramento da sessão");
        }

        votoRepository.deleteById(id);
    }

    @Override
    public ResultadoResponseDTO obterResultado(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + pautaId));

        ResultadoResponseDTO resultado = new ResultadoResponseDTO();
        resultado.setPautaId(pautaId);
        resultado.setTitulo(pauta.getTitulo());
        resultado.setVotosSim(votoRepository.countVotosSim(pautaId));
        resultado.setVotosNao(votoRepository.countVotosNao(pautaId));
        return resultado;
    }

    private VotoResponseDTO toResponseDTO(Voto voto) {
        VotoResponseDTO dto = new VotoResponseDTO();
        dto.setId(voto.getId());
        dto.setPautaId(voto.getPauta().getId());
        dto.setAssociadoCpf(voto.getAssociadoCpf());
        dto.setVoto(voto.getVoto());
        return dto;
    }
}
package full.stack.back.service.impl;


import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.entity.Voto;
import full.stack.back.exception.BusinessException;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import full.stack.back.service.VotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotoServiceImpl implements VotoService {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public Voto votar(VotoRequestDTO votoDTO) {
        Pauta pauta = pautaRepository.findById(votoDTO.getPautaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + votoDTO.getPautaId()));

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(votoDTO.getPautaId());
        if (sessao == null) {
            throw new BusinessException("Nenhuma sessão de votação aberta para a pauta: " + votoDTO.getPautaId());
        }

        if (LocalDateTime.now().isAfter(sessao.getFim())) {
            throw new BusinessException("Sessão de votação encerrada para a pauta: " + votoDTO.getPautaId());
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(votoDTO.getPautaId(), Long.valueOf(votoDTO.getCpf()))) {
            throw new BusinessException("Associado já votou na pauta: " + votoDTO.getPautaId());
        }

        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setAssociadoId(Long.valueOf(votoDTO.getCpf()));
        voto.setVoto(votoDTO.getVoto());
        return votoRepository.save(voto);
    }

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
}

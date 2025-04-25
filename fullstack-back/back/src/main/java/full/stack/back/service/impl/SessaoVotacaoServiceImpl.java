package full.stack.back.service.impl;


import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.exception.BusinessException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.service.SessaoVotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoServiceImpl implements SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    @Override
    public SessaoVotacao abrirSessao(SessaoVotacaoRequestDTO dto) {
        Pauta pauta = pautaRepository.findById(dto.getPautaId())
                .orElseThrow(() -> new BusinessException("Pauta não encontrada: " + dto.getPautaId()));

        if (sessaoVotacaoRepository.findByPautaId(dto.getPautaId()) != null) {
            throw new BusinessException("Sessão de votação já existe para a pauta: " + dto.getPautaId());
        }

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.now());
        int duracao = dto.getDuracaoMinutos() != null ? dto.getDuracaoMinutos() : 1;
        sessao.setFim(LocalDateTime.now().plusMinutes(duracao));

        return sessaoVotacaoRepository.save(sessao);
    }
}

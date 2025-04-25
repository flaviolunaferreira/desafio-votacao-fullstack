package full.stack.back.service.impl;

import full.stack.back.dto.BaixaParticipacaoDTO;
import full.stack.back.dto.DashboardResumoDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import full.stack.back.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    @Override
    public DashboardResumoDTO obterResumo() {
        DashboardResumoDTO resumo = new DashboardResumoDTO();

        // Total de pautas
        resumo.setTotalPautas(pautaRepository.count());

        // Total de sessões abertas e encerradas
        long totalSessoesAbertas = sessaoVotacaoRepository.countByDataFechamentoAfter(LocalDateTime.now());
        resumo.setTotalSessoesAbertas(totalSessoesAbertas);
        resumo.setTotalSessoesEncerradas(sessaoVotacaoRepository.count() - totalSessoesAbertas);

        // Total de votos e percentuais
        long totalVotos = votoRepository.count();
        resumo.setTotalVotos(totalVotos);
        if (totalVotos > 0) {
            long votosSim = votoRepository.countVotosSim();
            resumo.setPercentualVotosSim((double) votosSim / totalVotos * 100);
            resumo.setPercentualVotosNao(100 - resumo.getPercentualVotosSim());
        } else {
            resumo.setPercentualVotosSim(0.0);
            resumo.setPercentualVotosNao(0.0);
        }

        // Pautas recentes (últimas 5)
        List<Pauta> pautasRecentes = pautaRepository.findAllByOrderByIdDesc(PageRequest.of(0, 5));
        resumo.setPautasRecentes(pautasRecentes.stream()
                .map(pauta -> {
                    DashboardResumoDTO.PautaResumoDTO dto = new DashboardResumoDTO.PautaResumoDTO();
                    dto.setId(pauta.getId());
                    dto.setTitulo(pauta.getTitulo());
                    dto.setTotalVotos(votoRepository.countByPautaId(pauta.getId()));
                    return dto;
                })
                .collect(Collectors.toList()));

        // Sessões ativas
        List<SessaoVotacao> sessoesAtivas = sessaoVotacaoRepository.findByDataFechamentoAfter(LocalDateTime.now());
        resumo.setSessoesAtivas(sessoesAtivas.stream()
                .map(sessao -> {
                    DashboardResumoDTO.SessaoResumoDTO dto = new DashboardResumoDTO.SessaoResumoDTO();
                    dto.setId(sessao.getId());
                    dto.setPautaId(sessao.getPauta().getId());
                    dto.setPautaTitulo(sessao.getPauta().getTitulo());
                    long minutosRestantes = Duration.between(LocalDateTime.now(), sessao.getDataFechamento()).toMinutes();
                    dto.setTempoRestante(minutosRestantes > 0 ? minutosRestantes + " minutos" : "Menos de 1 minuto");
                    return dto;
                })
                .collect(Collectors.toList()));

        return resumo;
    }

    @Override
    public List<BaixaParticipacaoDTO> obterBaixaParticipacao() {
        List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll();
        return sessoes.stream()
                .map(sessao -> {
                    long totalVotos = votoRepository.countByPautaId(sessao.getPauta().getId());
                    long totalAssociados = 100; // Suposição: número fixo ou obtido de outra fonte
                    double percentual = (double) totalVotos / totalAssociados * 100;
                    if (percentual < 50) {
                        BaixaParticipacaoDTO dto = new BaixaParticipacaoDTO();
                        dto.setPautaId(sessao.getPauta().getId());
                        dto.setPautaTitulo(sessao.getPauta().getTitulo());
                        dto.setTotalVotos(totalVotos);
                        dto.setPercentualParticipacao(percentual);
                        return dto;
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

}
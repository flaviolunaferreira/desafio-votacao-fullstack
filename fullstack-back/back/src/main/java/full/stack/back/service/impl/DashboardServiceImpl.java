package full.stack.back.service.impl;

import full.stack.back.dto.DashboardResumoDTO;
import full.stack.back.dto.ParticipacaoSessaoDTO;
import full.stack.back.dto.TendenciaVotosDTO;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.exception.BusinessException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import full.stack.back.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    private static final long TOTAL_ASSOCIADOS = 100; // Suposição: ajustar conforme necessário

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
        List<DashboardResumoDTO.PautaResumoDTO> pautasRecentes = pautaRepository.findAllByOrderByIdDesc(PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(pauta -> {
                    DashboardResumoDTO.PautaResumoDTO dto = new DashboardResumoDTO.PautaResumoDTO();
                    dto.setId(pauta.getId());
                    dto.setTitulo(pauta.getTitulo());
                    dto.setTotalVotos(votoRepository.countBySessaoVotacao_Id(pauta.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

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
    public List<TendenciaVotosDTO> obterTendenciaVotos(String inicio, String fim, String granularidade) {
        // Validar datas
        LocalDate dataInicio, dataFim;
        try {
            dataInicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_LOCAL_DATE);
            dataFim = LocalDate.parse(fim, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new BusinessException("Formato de data inválido. Use YYYY-MM-DD");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data de início deve ser anterior à data de fim");
        }

        // Validar granularidade
        granularidade = granularidade.toUpperCase();
        if (!List.of("DIA", "SEMANA", "MES").contains(granularidade)) {
            throw new BusinessException("Granularidade deve ser DIA, SEMANA ou MES");
        }

        // Consultar votos no período
        List<Object[]> resultados = votoRepository.countVotosByPeriodo(dataInicio.atStartOfDay(), dataFim.plusDays(1).atStartOfDay(), granularidade);
        List<TendenciaVotosDTO> tendencias = new ArrayList<>();

        // Mapear resultados
        DateTimeFormatter formatter = switch (granularidade) {
            case "DIA" -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case "SEMANA" -> DateTimeFormatter.ofPattern("yyyy-'W'ww");
            case "MES" -> DateTimeFormatter.ofPattern("yyyy-MM");
            default -> DateTimeFormatter.ISO_LOCAL_DATE;
        };

        for (Object[] resultado : resultados) {
            TendenciaVotosDTO dto = new TendenciaVotosDTO();
            LocalDateTime data = ((java.sql.Timestamp) resultado[0]).toLocalDateTime();
            dto.setPeriodo(data.format(formatter));
            dto.setVotosSim((Long) resultado[1]);
            dto.setVotosNao((Long) resultado[2]);
            tendencias.add(dto);
        }

        return tendencias;
    }

    @Override
    public List<DashboardResumoDTO.PautaResumoDTO> obterPautasEngajamento(int limite) {
        List<Object[]> pautasMaisVotadas = pautaRepository.findPautasMaisVotadas(PageRequest.of(0, limite));
        return pautasMaisVotadas.stream()
                .map(resultado -> {
                    DashboardResumoDTO.PautaResumoDTO dto = new DashboardResumoDTO.PautaResumoDTO();
                    dto.setId((Long) resultado[0]);
                    dto.setTitulo((String) resultado[1]);
                    dto.setTotalVotos((Long) resultado[2]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipacaoSessaoDTO> obterParticipacaoSessoes() {
        List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll();
        return sessoes.stream()
                .map(sessao -> {
                    ParticipacaoSessaoDTO dto = new ParticipacaoSessaoDTO();
                    dto.setSessaoId(sessao.getId());
                    dto.setPautaId(sessao.getPauta().getId());
                    dto.setPautaTitulo(sessao.getPauta().getTitulo());
                    long totalVotos = votoRepository.countBySessaoVotacao_Id(sessao.getPauta().getId());
                    dto.setTotalVotos(totalVotos);
                    dto.setPercentualParticipacao((double) totalVotos / TOTAL_ASSOCIADOS * 100);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    private static final long TOTAL_ASSOCIADOS = 50; // Suposição: ajustar conforme necessário

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
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info("Obtendo tendência de votos: inicio={}, fim={}, granularidade={}", inicio, fim, granularidade);

        // Validar datas
        LocalDate dataInicio, dataFim;
        try {
            dataInicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_LOCAL_DATE);
            dataFim = LocalDate.parse(fim, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.error("Formato de data inválido: {}", e.getMessage());
            throw new BusinessException("Formato de data inválido. Use YYYY-MM-DD");
        }
        if (dataInicio.isAfter(dataFim)) {
            log.warn("Data de início posterior à data de fim: {} > {}", inicio, fim);
            throw new BusinessException("Data de início deve ser anterior ou igual à data de fim");
        }

        // Validar granularidade
        granularidade = granularidade.toUpperCase();
        if (!List.of("DIA", "SEMANA", "MES").contains(granularidade)) {
            log.warn("Granularidade inválida: {}", granularidade);
            throw new BusinessException("Granularidade deve ser DIA, SEMANA ou MES");
        }

        // Consultar votos no período
        LocalDateTime inicioPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimPeriodo = dataFim.atTime(23, 59, 59, 999999999); // Incluir todo o dia de fim
        List<Object[]> resultados = votoRepository.countVotosByPeriodo(inicioPeriodo, fimPeriodo, granularidade);
        log.debug("Resultados da query: {} registros", resultados.size());

        // Mapear resultados
        List<TendenciaVotosDTO> tendencias = new ArrayList<>();
        for (Object[] resultado : resultados) {
            log.debug("Processando resultado: periodo={}, votosSim={}, votosNao={}",
                    resultado[0], resultado[1], resultado[2]);
            TendenciaVotosDTO dto = new TendenciaVotosDTO();
            String periodo;

            if ("DIA".equals(granularidade)) {
                LocalDate date;
                if (resultado[0] instanceof java.sql.Timestamp) {
                    date = ((java.sql.Timestamp) resultado[0]).toLocalDateTime().toLocalDate();
                } else if (resultado[0] instanceof java.sql.Date) {
                    date = ((java.sql.Date) resultado[0]).toLocalDate();
                } else {
                    log.error("Tipo inesperado para periodo em DIA: {}", resultado[0].getClass().getName());
                    throw new BusinessException("Erro ao processar período: tipo de dado inválido");
                }
                periodo = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if ("SEMANA".equals(granularidade)) {
                periodo = (String) resultado[0]; // e.g., 2025-17
            } else {
                periodo = (String) resultado[0]; // e.g., 2025-04
            }

            dto.setPeriodo(periodo);
            dto.setVotosSim(((Number) resultado[1]).longValue());
            dto.setVotosNao(((Number) resultado[2]).longValue());
            tendencias.add(dto);
        }

        log.info("Tendências retornadas: {} registros", tendencias.size());
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
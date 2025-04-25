package full.stack.back.controller;

import full.stack.back.dto.DashboardResumoDTO;
import full.stack.back.dto.ParticipacaoSessaoDTO;
import full.stack.back.dto.TendenciaVotosDTO;
import full.stack.back.exception.BusinessException;
import full.stack.back.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumo")
    @Operation(summary = "Obtém resumo para o dashboard", description = "Retorna métricas agregadas para o dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DashboardResumoDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<DashboardResumoDTO> obterResumo() {
        DashboardResumoDTO resumo = dashboardService.obterResumo();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/votos/tendencia")
    @Operation(summary = "Obtém tendências de votação por período", description = "Retorna contagem de votos Sim/Não por dia, semana ou mês")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tendências retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TendenciaVotosDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Período inválido", value = "{\"message\": \"Data de início deve ser anterior à data de fim\"}"),
                                    @ExampleObject(name = "Granularidade inválida", value = "{\"message\": \"Granularidade deve ser DIA, SEMANA ou MES\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<TendenciaVotosDTO>> obterTendenciaVotos(
            @RequestParam String inicio,
            @RequestParam String fim,
            @RequestParam(defaultValue = "DIA") String granularidade) {
        List<TendenciaVotosDTO> tendencias = dashboardService.obterTendenciaVotos(inicio, fim, granularidade);
        return ResponseEntity.ok(tendencias);
    }

    @GetMapping("/pautas/engajamento")
    @Operation(summary = "Obtém pautas com maior engajamento", description = "Retorna pautas ordenadas por número de votos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pautas retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DashboardResumoDTO.PautaResumoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Limite inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Limite deve ser um número positivo\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<DashboardResumoDTO.PautaResumoDTO>> obterPautasEngajamento(
            @RequestParam(defaultValue = "10") int limite) {
        if (limite <= 0) {
            throw new BusinessException("Limite deve ser um número positivo");
        }
        List<DashboardResumoDTO.PautaResumoDTO> pautas = dashboardService.obterPautasEngajamento(limite);
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("/sessoes/participacao")
    @Operation(summary = "Obtém participação por sessão", description = "Retorna percentual de participação em cada sessão")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participações retornadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ParticipacaoSessaoDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<ParticipacaoSessaoDTO>> obterParticipacaoSessoes() {
        List<ParticipacaoSessaoDTO> participacoes = dashboardService.obterParticipacaoSessoes();
        return ResponseEntity.ok(participacoes);
    }
}
package full.stack.back.controller;

import full.stack.back.dto.BaixaParticipacaoDTO;
import full.stack.back.dto.DashboardResumoDTO;
import full.stack.back.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/baixa-participacao")
    @Operation(summary = "Obtém pautas com baixa participação", description = "Retorna pautas com menos de 50% de participação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaixaParticipacaoDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<BaixaParticipacaoDTO>> obterBaixaParticipacao() {
        List<BaixaParticipacaoDTO> result = dashboardService.obterBaixaParticipacao();
        return ResponseEntity.ok(result);
    }
}
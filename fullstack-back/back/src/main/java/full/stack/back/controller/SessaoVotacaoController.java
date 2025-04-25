package full.stack.back.controller;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessoes")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abre uma sessão de votação", description = "Inicia uma sessão de votação para uma pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SessaoVotacao.class))),
        @ApiResponse(responseCode = "400", description = "Pauta inválida ou sessão já existe",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"Sessão de votação já existe para a pauta: 1\"}"))),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}")))
    })
    public SessaoVotacao abrirSessao(@Valid @RequestBody SessaoVotacaoRequestDTO dto) {
        return sessaoVotacaoService.abrirSessao(dto);
    }
}

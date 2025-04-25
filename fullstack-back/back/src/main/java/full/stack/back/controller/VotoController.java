package full.stack.back.controller;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.entity.Voto;
import full.stack.back.service.VotoService;
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
@RequestMapping("/api/votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra um voto", description = "Registra o voto de um associado em uma pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Voto.class))),
        @ApiResponse(responseCode = "400", description = "Erro na votação (sessão inválida, CPF inválido, etc.)",
            content = @Content(mediaType = "application/json",
               examples = {
                  @ExampleObject(name = "Sessão encerrada", value = "{\"message\": \"Sessão de votação encerrada para a pauta: 1\"}"),
                  @ExampleObject(name = "CPF inválido", value = "{\"message\": \"CPF inválido: 12345678900\"}"),
                  @ExampleObject(name = "Associado já votou", value = "{\"message\": \"Associado já votou na pauta: 1\"}")
               })),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}")))
    })
    public Voto votar(@Valid @RequestBody VotoRequestDTO votoDTO) {
        return votoService.votar(votoDTO);
    }


    @GetMapping("/resultado/{pautaId}")
    @Operation(summary = "Obtém o resultado da votação", description = "Retorna o resultado da votação de uma pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResultadoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}")))
    })
    public ResultadoResponseDTO obterResultado(@PathVariable Long pautaId) {
        return votoService.obterResultado(pautaId);
    }

}

package full.stack.back.controller;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.service.PautaService;
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
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma nova pauta", description = "Cria uma nova pauta para votação")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Pauta.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"O título da pauta é obrigatório\"}")))
    })
    public Pauta criarPauta(@Valid @RequestBody PautaRequestDTO pautaDTO) {
        return pautaService.criarPauta(pautaDTO);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pauta por ID", description = "Retorna os detalhes de uma pauta específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pauta encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pauta.class))),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}")))
    })
    public Pauta buscarPauta(@PathVariable Long id) {
        return pautaService.buscarPauta(id);
    }


}

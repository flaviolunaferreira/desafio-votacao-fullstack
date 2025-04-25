package full.stack.back.controller;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.dto.PautaResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    @Operation(summary = "Cadastra uma nova pauta", description = "Cria uma nova pauta para votação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PautaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"titulo: O título da pauta é obrigatório; descricao: A descrição da pauta é obrigatória\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<PautaResponseDTO> criarPauta(@Valid @RequestBody PautaRequestDTO pautaDTO) {
        PautaResponseDTO responseDTO = pautaService.criarPauta(pautaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pauta por ID", description = "Retorna os detalhes de uma pauta específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PautaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<PautaResponseDTO> buscarPauta(@PathVariable Long id) {
        PautaResponseDTO responseDTO = pautaService.buscarPauta(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todas as pautas", description = "Retorna uma lista de todas as pautas cadastradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso (pode ser vazia)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PautaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<PautaResponseDTO>> listarPautas() {
        List<PautaResponseDTO> responseDTOs = pautaService.listarPautas();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma pauta", description = "Atualiza os dados de uma pauta existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PautaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"titulo: O título da pauta é obrigatório; descricao: A descrição da pauta é obrigatória\"}"))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<PautaResponseDTO> atualizarPauta(@PathVariable Long id, @Valid @RequestBody PautaRequestDTO pautaDTO) {
        PautaResponseDTO responseDTO = pautaService.atualizarPauta(id, pautaDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma pauta", description = "Remove uma pauta específica pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pauta deletada com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<Void> deletarPauta(@PathVariable Long id) {
        pautaService.deletarPauta(id);
        return ResponseEntity.noContent().build();
    }
}
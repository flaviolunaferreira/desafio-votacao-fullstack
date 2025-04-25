package full.stack.back.controller;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.dto.SessaoVotacaoResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessoes")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping
    @Operation(summary = "Abre uma sessão de votação", description = "Inicia uma sessão de votação para uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessaoVotacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou sessão já existe",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validação", value = "{\"message\": \"pautaId: O ID da pauta é obrigatório; duracao: A duração deve ser um número positivo\"}"),
                                    @ExampleObject(name = "Sessão existente", value = "{\"message\": \"Sessão de votação já existe para a pauta: 1\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<SessaoVotacaoResponseDTO> abrirSessao(@Valid @RequestBody SessaoVotacaoRequestDTO dto) {
        SessaoVotacaoResponseDTO responseDTO = sessaoVotacaoService.abrirSessao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma sessão por ID", description = "Retorna os detalhes de uma sessão de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessaoVotacaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Sessão não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<SessaoVotacaoResponseDTO> buscarSessao(@PathVariable Long id) {
        SessaoVotacaoResponseDTO responseDTO = sessaoVotacaoService.buscarSessao(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todas as sessões", description = "Retorna uma lista de todas as sessões de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso (pode ser vazia)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessaoVotacaoResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<SessaoVotacaoResponseDTO>> listarSessoes() {
        List<SessaoVotacaoResponseDTO> responseDTOs = sessaoVotacaoService.listarSessoes();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma sessão", description = "Atualiza os dados de uma sessão de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessaoVotacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"pautaId: O ID da pauta é obrigatório; duracao: A duração deve ser um número positivo\"}"))),
            @ApiResponse(responseCode = "404", description = "Sessão ou pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Sessão não encontrada", value = "{\"message\": \"Sessão não encontrada: 1\"}"),
                                    @ExampleObject(name = "Pauta não encontrada", value = "{\"message\": \"Pauta não encontrada: 1\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<SessaoVotacaoResponseDTO> atualizarSessao(@PathVariable Long id, @Valid @RequestBody SessaoVotacaoRequestDTO dto) {
        SessaoVotacaoResponseDTO responseDTO = sessaoVotacaoService.atualizarSessao(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma sessão", description = "Remove uma sessão de votação pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sessão deletada com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Sessão não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<Void> deletarSessao(@PathVariable Long id) {
        sessaoVotacaoService.deletarSessao(id);
        return ResponseEntity.noContent().build();
    }
}
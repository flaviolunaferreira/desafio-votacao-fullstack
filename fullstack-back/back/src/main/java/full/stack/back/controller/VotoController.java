package full.stack.back.controller;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Registra um voto", description = "Registra o voto de um associado em uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VotoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro na votação (sessão inválida, CPF inválido, etc.)",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validação", value = "{\"message\": \"pautaId: O ID da pauta é obrigatório; cpf: O CPF do associado é obrigatório; voto: O voto (Sim/Não) é obrigatório\"}"),
                                    @ExampleObject(name = "Sessão encerrada", value = "{\"message\": \"Sessão de votação encerrada para a pauta: 1\"}"),
                                    @ExampleObject(name = "CPF inválido", value = "{\"message\": \"CPF inválido: 12345678900\"}"),
                                    @ExampleObject(name = "Associado já votou", value = "{\"message\": \"Associado já votou na pauta: 1\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<VotoResponseDTO> votar(@Valid @RequestBody VotoRequestDTO votoDTO) {
        VotoResponseDTO responseDTO = votoService.votar(votoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um voto por ID", description = "Retorna os detalhes de um voto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VotoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Voto não encontrado: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<VotoResponseDTO> buscarVoto(@PathVariable Long id) {
        VotoResponseDTO responseDTO = votoService.buscarVoto(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todos os votos", description = "Retorna uma lista de todos os votos, opcionalmente filtrada por pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de votos retornada com sucesso (pode ser vazia)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VotoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada (se pautaId fornecido)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<List<VotoResponseDTO>> listarVotos(@RequestParam(required = false) Long pautaId) {
        List<VotoResponseDTO> responseDTOs = votoService.listarVotos(pautaId);
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um voto", description = "Atualiza os dados de um voto existente, se a sessão ainda estiver aberta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VotoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro na votação (sessão inválida, CPF inválido, etc.)",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validação", value = "{\"message\": \"pautaId: O ID da pauta é obrigatório; cpf: O CPF do associado é obrigatório; voto: O voto (Sim/Não) é obrigatório\"}"),
                                    @ExampleObject(name = "Sessão encerrada", value = "{\"message\": \"Sessão de votação encerrada para a pauta: 1\"}"),
                                    @ExampleObject(name = "CPF inválido", value = "{\"message\": \"CPF inválido: 12345678900\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Voto ou pauta não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Voto não encontrado", value = "{\"message\": \"Voto não encontrado: 1\"}"),
                                    @ExampleObject(name = "Pauta não encontrada", value = "{\"message\": \"Pauta não encontrada: 1\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<VotoResponseDTO> atualizarVoto(@PathVariable Long id, @Valid @RequestBody VotoRequestDTO votoDTO) {
        VotoResponseDTO responseDTO = votoService.atualizarVoto(id, votoDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um voto", description = "Remove um voto específico, se a sessão ainda estiver aberta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Voto deletado com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Sessão encerrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Não é possível deletar voto após o encerramento da sessão\"}"))),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Voto não encontrado: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<Void> deletarVoto(@PathVariable Long id) {
        votoService.deletarVoto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resultado/{pautaId}")
    @Operation(summary = "Obtém o resultado da votação", description = "Retorna o resultado da votação de uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultadoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Pauta não encontrada: 1\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor\"}")))
    })
    public ResponseEntity<ResultadoResponseDTO> obterResultado(@PathVariable Long pautaId) {
        ResultadoResponseDTO resultado = votoService.obterResultado(pautaId);
        return ResponseEntity.ok(resultado);
    }
}
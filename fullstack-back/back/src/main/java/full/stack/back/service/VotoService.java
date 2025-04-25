package full.stack.back.service;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;

import java.util.List;

public interface VotoService {
    VotoResponseDTO votar(VotoRequestDTO votoDTO);
    VotoResponseDTO buscarVoto(Long id);
    List<VotoResponseDTO> listarVotos(Long pautaId);
    VotoResponseDTO atualizarVoto(Long id, VotoRequestDTO votoDTO);
    void deletarVoto(Long id);
    ResultadoResponseDTO obterResultado(Long pautaId);
}
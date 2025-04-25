package full.stack.back.service;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.dto.PautaResponseDTO;

import java.util.List;

public interface PautaService {
    PautaResponseDTO criarPauta(PautaRequestDTO pautaDTO);
    PautaResponseDTO buscarPauta(Long id);
    List<PautaResponseDTO> listarPautas();
    PautaResponseDTO atualizarPauta(Long id, PautaRequestDTO pautaDTO);
    void deletarPauta(Long id);
}
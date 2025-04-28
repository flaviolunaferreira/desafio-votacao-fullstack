package full.stack.back.service;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;

import java.util.List;

public interface VotoService {
    VotoResponseDTO votar(VotoRequestDTO votoDTO);
    VotoResponseDTO buscarVoto(String cpf);
    List<VotoResponseDTO> listarVotos(Long sessaoId);
    VotoResponseDTO atualizarVoto(Long id, VotoRequestDTO votoDTO);
    void deletarVoto(Long id);
    ResultadoResponseDTO obterResultado(Long pautaId);

    Boolean verificaVotoAndSessao(String cpf, Long sessaoId);
}
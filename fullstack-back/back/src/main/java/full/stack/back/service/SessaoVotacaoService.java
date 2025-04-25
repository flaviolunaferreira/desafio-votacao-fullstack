package full.stack.back.service;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.dto.SessaoVotacaoResponseDTO;

import java.util.List;

public interface SessaoVotacaoService {
    SessaoVotacaoResponseDTO abrirSessao(SessaoVotacaoRequestDTO dto);
    SessaoVotacaoResponseDTO buscarSessao(Long id);
    List<SessaoVotacaoResponseDTO> listarSessoes();
    SessaoVotacaoResponseDTO atualizarSessao(Long id, SessaoVotacaoRequestDTO dto);
    void deletarSessao(Long id);
}
package full.stack.back.service;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.entity.Pauta;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PautaService {

    Pauta criarPauta(@Valid PautaRequestDTO pautaDTO);
    Pauta buscarPauta(Long id);
    Pauta atualizarPauta(Long id, @Valid PautaRequestDTO pautaDTO);
    void deletarPauta(Long id);
    List<Pauta> buscarPautas();
}

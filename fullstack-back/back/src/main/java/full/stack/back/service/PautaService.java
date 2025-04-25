package full.stack.back.service;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.entity.Pauta;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface PautaService {

    Pauta criarPauta(@Valid PautaRequestDTO pautaDTO);
    Pauta buscarPauta(Long id);
}

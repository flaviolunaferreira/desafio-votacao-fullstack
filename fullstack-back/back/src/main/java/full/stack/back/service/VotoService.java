package full.stack.back.service;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.entity.Voto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface VotoService {

    Voto votar(@Valid VotoRequestDTO votoDTO);
    ResultadoResponseDTO obterResultado(Long pautaId);

}

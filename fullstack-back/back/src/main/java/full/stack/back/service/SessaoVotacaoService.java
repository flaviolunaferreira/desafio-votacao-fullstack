package full.stack.back.service;

import full.stack.back.dto.SessaoVotacaoRequestDTO;
import full.stack.back.entity.SessaoVotacao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface SessaoVotacaoService {

    SessaoVotacao abrirSessao(@Valid SessaoVotacaoRequestDTO dto);

}

package full.stack.back.service;

import full.stack.back.dto.BaixaParticipacaoDTO;
import full.stack.back.dto.DashboardResumoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {
    DashboardResumoDTO obterResumo();

    List<BaixaParticipacaoDTO> obterBaixaParticipacao();
}
package full.stack.back.service;

import full.stack.back.dto.DashboardResumoDTO;
import full.stack.back.dto.ParticipacaoSessaoDTO;
import full.stack.back.dto.TendenciaVotosDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {

    DashboardResumoDTO obterResumo();
    List<DashboardResumoDTO.PautaResumoDTO> obterPautasEngajamento(int limite);
    List<ParticipacaoSessaoDTO> obterParticipacaoSessoes();
    List<TendenciaVotosDTO> obterTendenciaVotos(String inicio, String fim, String granularidade);

}
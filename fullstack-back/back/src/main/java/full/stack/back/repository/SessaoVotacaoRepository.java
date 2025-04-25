package full.stack.back.repository;

import full.stack.back.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    boolean existsByPautaId(Long pautaId);
    SessaoVotacao findByPautaId(Long pautaId);
    long countByDataFechamentoAfter(LocalDateTime data);
    List<SessaoVotacao> findByDataFechamentoAfter(LocalDateTime data);
}
package full.stack.back.repository;

import full.stack.back.dto.SessaoAbertaResponseDTO;
import full.stack.back.entity.SessaoVotacao;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    boolean existsByPautaId(Long pautaId);
    SessaoVotacao findByPautaId(Long pautaId);
    long countByDataFechamentoAfter(LocalDateTime data);
    List<SessaoVotacao> findByDataFechamentoAfter(LocalDateTime data);

    @Query("SELECT s FROM SessaoVotacao s WHERE s.dataAbertura IS NULL OR s.dataFechamento > :now")
    List<SessaoVotacao> findSessoesAbertas(LocalDateTime now);

}
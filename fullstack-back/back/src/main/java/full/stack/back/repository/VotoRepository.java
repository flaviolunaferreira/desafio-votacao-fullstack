package full.stack.back.repository;

import full.stack.back.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByPautaIdAndAssociadoId(Long pautaId, Long associadoId);
    List<Voto> findByPautaId(Long pautaId);
    long countByPautaId(Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.voto = true")
    long countVotosSim();

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = true")
    Long countVotosSim(Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = false")
    Long countVotosNao(Long pautaId);
}
package full.stack.back.repository;

import full.stack.back.entity.Voto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VotoRepository extends CrudRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, Long cpfAssociado);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = true")
    Long countVotosSim(Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = false")
    Long countVotosNao(Long pautaId);

}

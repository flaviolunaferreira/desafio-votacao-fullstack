package full.stack.back.repository;

import full.stack.back.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    boolean existsByPautaIdAndAssociadoCpf(Long pautaId, String associadoCpf);

    List<Voto> findByPautaId(Long pautaId);
    long countByPautaId(Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.voto = true")
    long countVotosSim();

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = true")
    Long countVotosSim(Long pautaId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = false")
    Long countVotosNao(Long pautaId);

    @Query(value = "SELECT " +
            "CASE :granularidade " +
            "  WHEN 'DIA' THEN DATE(v.data_voto) " +
            "  WHEN 'SEMANA' THEN DATE_FORMAT(v.data_voto, '%Y-%U') " +
            "  WHEN 'MES' THEN DATE_FORMAT(v.data_voto, '%Y-%m') " +
            "END AS periodo, " +
            "SUM(CASE WHEN v.voto = true THEN 1 ELSE 0 END) AS votos_sim, " +
            "SUM(CASE WHEN v.voto = false THEN 1 ELSE 0 END) AS votos_nao " +
            "FROM voto v " +
            "WHERE v.data_voto BETWEEN :inicio AND :fim " +
            "GROUP BY periodo " +
            "ORDER BY periodo", nativeQuery = true)
    List<Object[]> countVotosByPeriodo(LocalDateTime inicio, LocalDateTime fim, String granularidade);
}
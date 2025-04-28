package full.stack.back.repository;

import full.stack.back.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    @Query("SELECT COUNT(v) > 0 FROM Voto v WHERE v.sessaoVotacao = :sessaoVotacao AND v.associadoCpf = :cpf")
    boolean existsBySessaoVotacaoAndAssociadoCpf(Long sessaoVotacao, String cpf);

    List<Voto> findBySessaoVotacao_Id(Long sessaoVotacao);
    long countBySessaoVotacao_Id(Long sessaoVotacao);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.voto = true")
    long countVotosSim();

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.sessaoVotacao.id = :sessaoVotacao AND v.voto = true")
    Long countVotosSim(Long sessaoVotacao);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.sessaoVotacao.id = :sessaoVotacaoId AND v.voto = false")
    Long countVotosNao(Long sessaoVotacaoId);

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

    Voto findByassociadoCpf(String cpf);

    boolean existsByAssociadoCpfAndSessaoVotacaoId(String cpf, Long sessaoId);

}
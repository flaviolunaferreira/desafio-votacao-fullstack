package full.stack.back.repository;

import full.stack.back.entity.Pauta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
    Page<Pauta> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT p.id, p.titulo, COUNT(v.id) AS total_votos " +
            "FROM Pauta p LEFT JOIN Voto v ON p.id = v.pauta.id " +
            "GROUP BY p.id, p.titulo " +
            "ORDER BY total_votos DESC")
    List<Object[]> findPautasMaisVotadas(Pageable pageable);
}
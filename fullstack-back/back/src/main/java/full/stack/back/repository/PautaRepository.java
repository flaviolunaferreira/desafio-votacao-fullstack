package full.stack.back.repository;

import full.stack.back.entity.Pauta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PautaRepository extends CrudRepository<Pauta, Long> {
    List<Pauta> findAllByOrderByIdDesc(Pageable pageable);
}

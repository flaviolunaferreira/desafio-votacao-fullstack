package full.stack.back.repository;

import full.stack.back.entity.SessaoVotacao;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessaoVotacaoRepository extends CrudRepository<SessaoVotacao, Long> {
    SessaoVotacao findByPautaId(Long pautaId);
}

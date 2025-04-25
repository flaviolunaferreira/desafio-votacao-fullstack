package full.stack.back.service.impl;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PautaServiceImpl implements PautaService {

    @Autowired
    private final PautaRepository pautaRepository;

    @Override
    public Pauta criarPauta(PautaRequestDTO pautaDTO) {
        Pauta pauta = new Pauta();
        pauta.setTitulo(pautaDTO.getTitulo());
        pauta.setDescricao(pautaDTO.getDescricao());
        return pautaRepository.save(pauta);
    }

    @Override
    public Pauta buscarPauta(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + id));
    }

}

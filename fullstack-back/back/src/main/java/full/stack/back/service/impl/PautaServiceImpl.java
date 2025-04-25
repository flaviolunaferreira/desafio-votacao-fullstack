package full.stack.back.service.impl;

import full.stack.back.dto.PautaRequestDTO;
import full.stack.back.dto.PautaResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class PautaServiceImpl implements PautaService {

    private final PautaRepository pautaRepository;

    @Override
    public PautaResponseDTO criarPauta(PautaRequestDTO pautaDTO) {
        Pauta pauta = new Pauta();
        pauta.setTitulo(pautaDTO.getTitulo());
        pauta.setDescricao(pautaDTO.getDescricao());
        pauta = pautaRepository.save(pauta);
        return toResponseDTO(pauta);
    }

    @Override
    public PautaResponseDTO buscarPauta(Long id) {
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + id));
        return toResponseDTO(pauta);
    }

    @Override
    public List<PautaResponseDTO> listarPautas() {
        List<Pauta> pautas = (List<Pauta>) pautaRepository.findAll();
        return pautas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PautaResponseDTO atualizarPauta(Long id, PautaRequestDTO pautaDTO) {
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + id));
        pauta.setTitulo(pautaDTO.getTitulo());
        pauta.setDescricao(pautaDTO.getDescricao());
        pauta = pautaRepository.save(pauta);
        return toResponseDTO(pauta);
    }

    @Override
    public void deletarPauta(Long id) {
        if (!pautaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pauta não encontrada: " + id);
        }
        pautaRepository.deleteById(id);
    }

    private PautaResponseDTO toResponseDTO(Pauta pauta) {
        PautaResponseDTO dto = new PautaResponseDTO();
        dto.setId(pauta.getId());
        dto.setTitulo(pauta.getTitulo());
        dto.setDescricao(pauta.getDescricao());
        return dto;
    }
}
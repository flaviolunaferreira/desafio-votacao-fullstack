package full.stack.back.service.impl;

import full.stack.back.dto.ResultadoResponseDTO;
import full.stack.back.dto.SessaoAbertaResponseDTO;
import full.stack.back.dto.VotoRequestDTO;
import full.stack.back.dto.VotoResponseDTO;
import full.stack.back.entity.Pauta;
import full.stack.back.entity.SessaoVotacao;
import full.stack.back.entity.Voto;
import full.stack.back.exception.BusinessException;
import full.stack.back.exception.ResourceNotFoundException;
import full.stack.back.repository.PautaRepository;
import full.stack.back.repository.SessaoVotacaoRepository;
import full.stack.back.repository.VotoRepository;
import full.stack.back.service.VotoService;
import full.stack.back.util.CpfValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Validated
public class VotoServiceImpl implements VotoService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Override
    public VotoResponseDTO votar(VotoRequestDTO votoDTO) {
        log.info("Processando voto para CPF: {}, Sessão ID: {}", votoDTO.getCpf(), votoDTO.getSessaoId());

        // Validar entrada
        if (votoDTO.getSessaoId() == null) {
            log.warn("sessaoId é nulo");
            throw new BusinessException("O ID da sessão é obrigatório");
        }
        if (votoDTO.getCpf() == null || !CpfValidation.isValid(votoDTO.getCpf())) {
            log.warn("CPF inválido: {}", votoDTO.getCpf());
            throw new BusinessException("CPF inválido: " + votoDTO.getCpf());
        }
        if (votoDTO.getVoto() == null) {
            log.warn("Voto é nulo");
            throw new BusinessException("O campo voto é obrigatório e deve ser true ou false");
        }

        // Validar sessão
        SessaoVotacao sessao = sessaoVotacaoRepository.findById(votoDTO.getSessaoId())
                .orElseThrow(() -> {
                    log.error("Sessão não encontrada: {}", votoDTO.getSessaoId());
                    return new ResourceNotFoundException("Sessão de votação não encontrada: " + votoDTO.getSessaoId());
                });

        // Verificar integridade da sessão
        if (sessao.getId() == null) {
            log.error("Sessão carregada tem ID nulo: {}", sessao);
            throw new BusinessException("Sessão inválida: ID não pode ser nulo");
        }

        // Verificar se a sessão está aberta
        LocalDateTime now = LocalDateTime.now();
        if (sessao.getDataFechamento() == null || now.isAfter(sessao.getDataFechamento())) {
            log.warn("Sessão encerrada ou dataFechamento nula: {}", votoDTO.getSessaoId());
            throw new BusinessException("Sessão de votação encerrada para a sessão: " + votoDTO.getSessaoId());
        }

        // Verificar se o associado já votou
        if (votoRepository.existsByAssociadoCpfAndSessaoVotacaoId(votoDTO.getCpf(), votoDTO.getSessaoId())) {
            log.warn("Associado já votou na sessão: CPF {}, Sessão ID {}", votoDTO.getCpf(), votoDTO.getSessaoId());
            throw new BusinessException("Associado com CPF " + votoDTO.getCpf() + " já votou nesta sessão: " + votoDTO.getSessaoId());
        }

        // Registrar voto
        Voto voto = new Voto();
        voto.setSessaoVotacao(sessao);
        voto.setAssociadoCpf(votoDTO.getCpf());
        voto.setVoto(votoDTO.getVoto());
        voto.setDataVoto(now);

        try {
            Voto savedVoto = votoRepository.save(voto);
            log.info("Voto registrado com sucesso: ID {}", savedVoto.getId());
            return new VotoResponseDTO(savedVoto.getId(), savedVoto.getSessaoVotacao().getId(),
                    savedVoto.getAssociadoCpf(), savedVoto.getVoto());
        } catch (Exception e) {
            log.error("Erro ao salvar voto: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao registrar voto: " + e.getMessage());
        }
    }

    @Override
    public VotoResponseDTO buscarVoto(String cpf) {
        Voto voto = votoRepository.findByAssociadoCpf(cpf);
        if (voto == null ) {
            throw new ResourceNotFoundException("Voto não encontrado para o CPF: " + cpf);
        }
        return toResponseDTO(voto);
    }

    @Override
    public List<VotoResponseDTO> listarVotos(Long pautaId) {
        List<Voto> votos;
        if (pautaId != null) {
            pautaRepository.findById(pautaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + pautaId));
            votos = votoRepository.findBySessaoVotacao_Id(pautaId);
        } else {
            votos = votoRepository.findAll();
        }
        return votos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VotoResponseDTO atualizarVoto(Long id, VotoRequestDTO votoDTO) {
        // Validar CPF
        if (!CpfValidation.isValid(votoDTO.getCpf())) {
            throw new BusinessException("CPF inválido: " + votoDTO.getCpf());
        }

        // Verificar voto
        Voto voto = votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado: " + id));

        // Verificar pauta
        Pauta pauta = pautaRepository.findById(votoDTO.getSessaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + votoDTO.getSessaoId()));

        // Verificar sessão de votação
        SessaoVotacao sessao = sessaoVotacaoRepository.findOpenByPautaId(votoDTO.getSessaoId());
        if (sessao == null) {
            throw new BusinessException("Nenhuma sessão de votação aberta para a pauta: " + votoDTO.getSessaoId());
        }

        // Verificar se a sessão está aberta
        LocalDateTime now = LocalDateTime.now();
        if (sessao.getDataFechamento() != null && now.isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Sessão de votação encerrada para a pauta: " + votoDTO.getSessaoId());
        }

        // Atualizar voto
        voto.setSessaoVotacao(voto.getSessaoVotacao());
        voto.setAssociadoCpf(votoDTO.getCpf());
        voto.setVoto(votoDTO.getVoto());
        voto.setDataVoto(now);

        Voto updatedVoto = votoRepository.save(voto);
        return new VotoResponseDTO(updatedVoto.getId(), updatedVoto.getSessaoVotacao().getId(),
                updatedVoto.getAssociadoCpf(), updatedVoto.getVoto());
    }

    @Override
    public void deletarVoto(Long id) {
        Voto voto = votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado: " + id));

        SessaoVotacao sessao = sessaoVotacaoRepository.findOpenByPautaId(voto.getSessaoVotacao().getId());
        if (sessao != null && LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Não é possível deletar voto após o encerramento da sessão");
        }

        votoRepository.deleteById(id);
    }

    @Override
    public ResultadoResponseDTO obterResultado(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada: " + pautaId));

        ResultadoResponseDTO resultado = new ResultadoResponseDTO();
        resultado.setPautaId(pautaId);
        resultado.setTitulo(pauta.getTitulo());
        resultado.setVotosSim(votoRepository.countVotosSim(pautaId));
        resultado.setVotosNao(votoRepository.countVotosNao(pautaId));
        return resultado;
    }

    @Override
    public Boolean verificaVotoAndSessao(String cpf, Long sessaoId) {
        return votoRepository.existsByAssociadoCpfAndSessaoVotacaoId(cpf, sessaoId);

    }

    @Override
    public List<SessaoAbertaResponseDTO> listarSessoesAbertasSemVoto(String cpf) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info("Buscando sessões abertas sem voto para CPF: {}", cpf);

        if (cpf == null || cpf.trim().isEmpty()) {
            log.warn("CPF inválido fornecido: {}", cpf);
            throw new BusinessException("CPF é obrigatório");
        }

        List<SessaoVotacao> sessoesAbertas = sessaoVotacaoRepository.findSessoesAbertas(LocalDateTime.now());
        log.debug("Sessões abertas encontradas: {}", sessoesAbertas.size());

        return sessoesAbertas.stream()
                .filter(sessao -> {
                    if (sessao.getId() == null) {
                        log.warn("Sessão com ID nulo encontrada: {}", sessao);
                        return false;
                    }
                    boolean exists = votoRepository.existsByAssociadoCpfAndSessaoVotacaoId(cpf, sessao.getId());
                    log.debug("Sessão ID: {}, CPF: {}, Voto existe: {}", sessao.getId(), cpf, exists);
                    return !exists;
                })
                .filter(sessao -> sessao.getPauta() != null)
                .map(sessao -> new SessaoAbertaResponseDTO(
                        sessao.getId(),
                        sessao.getPauta().getId(),
                        sessao.getPauta().getTitulo() != null ? sessao.getPauta().getTitulo() : "Sem título",
                        sessao.getDataAbertura(),
                        sessao.getDataFechamento()
                ))
                .collect(Collectors.toList());
    }

    private VotoResponseDTO toResponseDTO(Voto voto) {
        VotoResponseDTO dto = new VotoResponseDTO();
        dto.setId(voto.getId());
        dto.setSessaoId(voto.getSessaoVotacao().getId());
        dto.setAssociadoCpf(voto.getAssociadoCpf());
        dto.setVoto(voto.getVoto());
        return dto;
    }
}
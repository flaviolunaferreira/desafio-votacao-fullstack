package full.stack.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessaoVotacaoResponseDTO {
    private Long id;
    private Long pautaId;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
}
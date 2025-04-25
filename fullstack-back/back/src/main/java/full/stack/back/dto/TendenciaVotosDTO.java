package full.stack.back.dto;

import lombok.Data;

@Data
public class TendenciaVotosDTO {
    private String periodo; // Ex.: "2025-01-01"
    private Long votosSim;
    private Long votosNao;
}
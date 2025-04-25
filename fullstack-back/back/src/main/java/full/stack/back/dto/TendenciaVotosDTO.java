package full.stack.back.dto;

import lombok.Data;

@Data
public class TendenciaVotosDTO {
    private String periodo; // Ex.: "2025-01-01" (dia), "2025-W01" (semana), "2025-01" (mês)
    private Long votosSim;
    private Long votosNao;
}
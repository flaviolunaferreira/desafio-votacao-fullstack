package full.stack.back.dto;

import lombok.Data;

@Data
public class ResultadoResponseDTO {

    private Long pautaId;
    private String titulo;
    private Long votosSim;
    private Long votosNao;

}

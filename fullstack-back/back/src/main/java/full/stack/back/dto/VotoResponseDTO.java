package full.stack.back.dto;

import lombok.Data;

@Data
public class VotoResponseDTO {
    private Long id;
    private Long pautaId;
    private Long associadoId;
    private Boolean voto; // true = Sim, false = Não
}
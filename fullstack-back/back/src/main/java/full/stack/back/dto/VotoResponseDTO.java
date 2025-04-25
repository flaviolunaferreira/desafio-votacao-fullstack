package full.stack.back.dto;

import lombok.Data;

@Data
public class VotoResponseDTO {
    private Long id;
    private Long pautaId;
    private String associadoCpf; // Alterado de Long associadoId
    private Boolean voto; // true = Sim, false = Não
}
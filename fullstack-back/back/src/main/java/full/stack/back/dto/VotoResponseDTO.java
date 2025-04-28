package full.stack.back.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotoResponseDTO {
    private Long id;
    private Long sessaoId;
    private String associadoCpf;
    private Boolean voto; // true = Sim, false = Não

}
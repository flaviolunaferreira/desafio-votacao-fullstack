package full.stack.back.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PautaRequestDTO {

    @NotBlank(message = "O título da pauta é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição da pauta é obrigatória")
    private String descricao;

}

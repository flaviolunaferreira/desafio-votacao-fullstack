package full.stack.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VotoRequestDTO {

    @NotNull(message = "O ID da pauta é obrigatório")
    private Long pautaId;

    @NotBlank(message = "O CPF do associado é obrigatório")
    private String cpf;

    @NotNull(message = "O voto (Sim/Não) é obrigatório")
    private Boolean voto;

}

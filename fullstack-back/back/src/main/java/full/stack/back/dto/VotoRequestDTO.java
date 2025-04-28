package full.stack.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VotoRequestDTO {

    @NotNull(message = "O ID da sessão é obrigatório")
    private Long sessaoId;

    @NotBlank(message = "O CPF do associado é obrigatório")
    private String cpf;

    @NotNull(message = "O voto (Sim/Não) é obrigatório")
    private Boolean voto;

}

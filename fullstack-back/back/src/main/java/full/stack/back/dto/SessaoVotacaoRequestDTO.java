package full.stack.back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessaoVotacaoRequestDTO {

    @NotNull(message = "O ID da pauta é obrigatório.")
    private Long pautaId;

    @Min(value = 60, message = "A duração deve ser de pelo menos 1 hora.")
    private Integer duracaoMinutos;

}

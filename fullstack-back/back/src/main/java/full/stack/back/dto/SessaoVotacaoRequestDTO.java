package full.stack.back.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SessaoVotacaoRequestDTO {
    @NotNull(message = "O ID da pauta é obrigatório")
    private Long pautaId;

    @Positive(message = "A duração deve ser um número positivo")
    private Integer duracao; // Em minutos


}
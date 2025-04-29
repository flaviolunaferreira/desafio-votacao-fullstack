package full.stack.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoAbertaResponseDTO {
    private Long id;
    private Long pautaId;
    private String pautaTitulo;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
}
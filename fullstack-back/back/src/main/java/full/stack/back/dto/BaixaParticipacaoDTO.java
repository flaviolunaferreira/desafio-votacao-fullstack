package full.stack.back.dto;

import lombok.Data;

@Data
public class BaixaParticipacaoDTO {
    private Long pautaId;
    private String pautaTitulo;
    private Long totalVotos;
    private Double percentualParticipacao;
}
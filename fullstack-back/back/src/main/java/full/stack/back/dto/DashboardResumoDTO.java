package full.stack.back.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResumoDTO {
    private Long totalPautas;
    private Long totalSessoesAbertas;
    private Long totalSessoesEncerradas;
    private Long totalVotos;
    private Double percentualVotosSim;
    private Double percentualVotosNao;
    private List<PautaResumoDTO> pautasRecentes;
    private List<SessaoResumoDTO> sessoesAtivas;

    @Data
    public static class PautaResumoDTO {
        private Long id;
        private String titulo;
        private Long totalVotos;
    }

    @Data
    public static class SessaoResumoDTO {
        private Long id;
        private Long pautaId;
        private String pautaTitulo;
        private String tempoRestante; // Ex.: "10 minutos"
    }
}
package full.stack.back.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    @NotNull(message = "A pauta é obrigatória")
    private Pauta pauta;

    @NotNull(message = "A data de abertura é obrigatória")
    private LocalDateTime dataAbertura;

    @NotNull(message = "A data de fechamento é obrigatória")
    private LocalDateTime dataFechamento;
}
package full.stack.back.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @NotNull(message = "O CPF do associado é obrigatório")
    private String associadoCpf; // Alterado de Long associadoId

    @NotNull(message = "O voto (Sim/Não) é obrigatório")
    private Boolean voto; // true = Sim, false = Não

    @NotNull(message = "A data do voto é obrigatória")
    private LocalDateTime dataVoto;
}
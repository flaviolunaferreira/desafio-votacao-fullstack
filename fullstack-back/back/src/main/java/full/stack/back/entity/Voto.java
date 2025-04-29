package full.stack.back.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "voto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sessao_id", "associado_cpf"})
})
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessaoVotacao;

    @Column(name = "associado_cpf", nullable = false)
    private String associadoCpf;

    @Column(nullable = false)
    private Boolean voto;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto;

}
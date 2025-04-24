package full.stack.back.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    private LocalDateTime inicio;

    private LocalDateTime fim;

}

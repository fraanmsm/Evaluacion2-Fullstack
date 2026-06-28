package cl.duoc.api_triage.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "BITACORA_CLINICA")
@Data
public class bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IDPACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ACCION", nullable = false)
    private String accion;

    @Column(name = "FECHAHORA", nullable = false)
    private LocalDateTime fechaHora;
}

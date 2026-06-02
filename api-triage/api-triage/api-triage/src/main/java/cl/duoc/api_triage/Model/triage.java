package cl.duoc.api_triage.Model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "pacientes")
@Data
@JsonPropertyOrder({ "id", "nombre", "apellidoPaterno", "apellidoMaterno", "gravedad", "estadoEspera", "activo" })
public class triage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del paciente no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido paterno no puede estar vacío")
    @Column(nullable = false)
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno no puede estar vacío")
    @Column(nullable = false)
    private String apellidoMaterno;

    @NotNull(message = "La gravedad no puede ser nula")
    @Min(value = 1, message = "La gravedad debe ser entre 1 y 5")
    @Max(value = 5, message = "La gravedad debe ser entre 1 y 5")
    private Integer gravedad;

    @Column(name = "estado_espera")
    private String estadoEspera;

    private boolean activo = true;
}
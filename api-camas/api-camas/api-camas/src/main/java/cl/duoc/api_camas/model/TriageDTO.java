package cl.duoc.api_camas.model;

import lombok.Data;

@Data
public class TriageDTO {

    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer gravedad;
    private boolean activo;
}

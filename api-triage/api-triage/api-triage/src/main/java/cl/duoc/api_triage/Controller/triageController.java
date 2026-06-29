package cl.duoc.api_triage.Controller;

import cl.duoc.api_triage.Model.triage;
import cl.duoc.api_triage.Service.triageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/triage")
@Tag(name = "Triage Clínico", description = "Endpoints interactivos para la admisión, flujo y auditoría de pacientes en urgencias")
public class triageController {

    @Autowired
    private triageService service;

    @GetMapping
    @Operation(summary = "Listar todos los pacientes activos", description = "Retorna la lista completa de pacientes que se encuentran actualmente en la sala de espera de urgencias.")
    public ResponseEntity<List<triage>> listar() {
        return ResponseEntity.ok(service.listarTodosActivos());
    }

    @GetMapping("/criticos")
    @Operation(summary = "Listar pacientes en estado crítico", description = "Filtra de forma exclusiva a los pacientes clasificados con Gravedad 1 o 2 para auditoría inmediata del Minsal.")
    public ResponseEntity<List<triage>> listarCriticos() {
        return ResponseEntity.ok(service.listarCriticos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente activo por ID", description = "Busca un paciente específico en la sala de espera utilizando su identificador único transaccional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado con éxito"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado o dado de alta de forma lógica")
    })
    public ResponseEntity<triage> verUno(
            @Parameter(description = "ID interno del paciente asignado en la admisión", example = "1") @PathVariable Long id) {
        return service.buscarActivoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar admisión de paciente", description = "Registra un nuevo paciente en el sistema, evaluando de forma distribuida y remota la disponibilidad en api-camas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paciente registrado con éxito y grabado en la Bitácora Clínica"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o paciente ya registrado con ese nombre completo")
    })
    public ResponseEntity<triage> registrar(@Valid @RequestBody triage paciente) {
        triage nuevo = service.registrar(paciente);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del paciente", description = "Permite modificar los datos demográficos y de prioridad de un paciente siempre que su gravedad original no sea crítica (1 o 2).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente modificado con éxito y evento registrado en la Bitácora de auditoría"),
            @ApiResponse(responseCode = "400", description = "Intento ilegal de alterar o degradar los datos de un paciente clínico en estado crítico")
    })
    public ResponseEntity<triage> actualizar(
            @Parameter(description = "ID del paciente a modificar en la base de datos", example = "3") @PathVariable Long id, 
            @Valid @RequestBody triage datos) {
        triage actualizado = service.actualizar(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de alta a un paciente (Soft Delete)", description = "Cambia el estado lógico del paciente a inactivo, liberando su cupo en el sistema siempre que no pertenezca a gravedad crítica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alta procesada correctamente y guardada en el historial inmutable de la bitácora"),
            @ApiResponse(responseCode = "400", description = "Prohibido por ley dar de alta a un paciente de gravedad 1 o 2 sin estabilización previa en el recinto")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del paciente a dar de alta médica", example = "4") @PathVariable Long id) {
        service.darDeAlta(id);
        return ResponseEntity.noContent().build();
    }
}

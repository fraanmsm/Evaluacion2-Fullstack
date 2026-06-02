package cl.duoc.api_triage.Controller;

import cl.duoc.api_triage.Model.triage;
import cl.duoc.api_triage.Service.triageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/triage")
public class triageController {

    @Autowired
    private triageService service;

    @GetMapping
    public ResponseEntity<List<triage>> listar() {
        return ResponseEntity.ok(service.listarTodosActivos());
    }

    @GetMapping("/criticos")
    public ResponseEntity<List<triage>> listarCriticos() {
        return ResponseEntity.ok(service.listarCriticos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<triage> verUno(@PathVariable Long id) {
        return service.buscarActivoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<triage> registrar(@Valid @RequestBody triage paciente) {
        triage nuevo = service.registrar(paciente);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<triage> actualizar(@PathVariable Long id, @Valid @RequestBody triage datos) {
        triage actualizado = service.actualizar(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.darDeAlta(id);
        return ResponseEntity.noContent().build();
    }
}
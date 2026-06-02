package cl.duoc.api_camas.controller;

import cl.duoc.api_camas.model.TriageDTO;
import cl.duoc.api_camas.service.CamaService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/camas")
public class CamaController {

    @Autowired
    private CamaService camaService;;

    @GetMapping("/monitoreo")
    public ResponseEntity<List<TriageDTO>> monitorearCriticos() {
        List<TriageDTO> listaCriticos = camaService.obtenerListaCriticos();
        return ResponseEntity.ok(listaCriticos);
    }

    @GetMapping("/disponibilidad/{gravedad}")
    public ResponseEntity<Boolean> verificarDisponibilidad(@PathVariable("gravedad") Integer gravedad) {
        boolean disponible = camaService.verificarCamaPorGravedad(gravedad);
        return ResponseEntity.ok(disponible);
    }
}


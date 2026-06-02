package cl.duoc.api_triage.Service;

import cl.duoc.api_triage.Model.triage;
import cl.duoc.api_triage.Repository.triageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

@Service
public class triageService {

    @Autowired
    private triageRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private final String CAMAS_API_URL = "http://localhost:25000/api/camas/disponibilidad/";

    public List<triage> listarTodosActivos() {
        return repository.findAll().stream()
                .filter(triage::isActivo)
                .toList();
    }

    public List<triage> listarCriticos() {
        return repository.findAll().stream()
                .filter(triage::isActivo)
                .filter(p -> p.getGravedad() == 1 || p.getGravedad() == 2)
                .toList();
    }

    public Optional<triage> buscarActivoPorId(Long id) {
        return repository.findById(id).filter(triage::isActivo);
    }

    @Transactional
    public triage registrar(triage nuevo) {
        Optional<triage> existente = repository.findByNombreAndApellidoPaternoAndApellidoMaterno(
                nuevo.getNombre(),
                nuevo.getApellidoPaterno(),
                nuevo.getApellidoMaterno()
        );

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un paciente con el nombre: " + nuevo.getNombre() + " " +
                    nuevo.getApellidoPaterno() + " " + nuevo.getApellidoMaterno());
        }

        try {
            String urlDestino = CAMAS_API_URL + nuevo.getGravedad();
            Boolean hayCamaDisponible = restTemplate.getForObject(urlDestino, Boolean.class);

            if (hayCamaDisponible != null && hayCamaDisponible) {
                nuevo.setEstadoEspera("EN ESPERA");
            } else {
                nuevo.setEstadoEspera("LISTA DE ESPERA CRÍTICA");
            }
        } catch (Exception e) {
            System.out.println("api-camas fuera de línea. Activando protocolo de contingencia.");
            nuevo.setEstadoEspera("LISTA DE ESPERA CRÍTICA");
        }

        return repository.save(nuevo);
    }

    public triage actualizar(Long id, triage datosNuevos) {
        triage existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (existente.getGravedad() == 1 || existente.getGravedad() == 2) {
            throw new RuntimeException("No se puede actualizar a un paciente con gravedad crítica (1 o 2)");
        }

        existente.setNombre(datosNuevos.getNombre());
        existente.setApellidoPaterno(datosNuevos.getApellidoPaterno());
        existente.setApellidoMaterno(datosNuevos.getApellidoMaterno());
        existente.setGravedad(datosNuevos.getGravedad());

        return repository.save(existente);
    }

    @Transactional
    public void darDeAlta(Long id) {
        triage paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (paciente.getGravedad() == 1 || paciente.getGravedad() == 2) {
            throw new RuntimeException("No se puede dar de alta a un paciente con gravedad crítica (1 o 2)");
        }

        paciente.setActivo(false);
        repository.save(paciente);
    }
}
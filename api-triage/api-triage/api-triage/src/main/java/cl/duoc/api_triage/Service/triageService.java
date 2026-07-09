package cl.duoc.api_triage.Service;

import cl.duoc.api_triage.Model.triage;
import cl.duoc.api_triage.Model.bitacora;
import cl.duoc.api_triage.Repository.triageRepository;
import cl.duoc.api_triage.Repository.bitacoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.List;
import java.util.Optional;

@Service
public class triageService {

    @Autowired
    private triageRepository repository;

   @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private bitacoraRepository bitacoraRepo; // Inyección de la bitácora

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
            rabbitTemplate.convertAndSend("cola-camas", nuevo);

            nuevo.setEstadoEspera("EN ESPERA");

        } catch (Exception e) {
            System.out.println("Error al enviar el mensaje a RabbitMQ.");
            nuevo.setEstadoEspera("LISTA DE ESPERA CRÍTICA");
        }

        triage guardado = repository.save(nuevo);

        // Registro en auditoría
        registrarEnBitacora(guardado.getId(), "INGRESO TRIAGE - Gravedad: " + guardado.getGravedad() + " - Estado: " + guardado.getEstadoEspera());

        return guardado;
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

        triage actualizado = repository.save(existente);

        // Registro en auditoría
        registrarEnBitacora(actualizado.getId(), "ACTUALIZACION DE DATOS - Nueva Gravedad: " + actualizado.getGravedad());

        return actualizado;
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

        // Registro en auditoría
        registrarEnBitacora(paciente.getId(), "EGRESO/ALTA MEDICA");
    }

    // Método privado centralizado para guardar el log
    private void registrarEnBitacora(Long idPaciente, String accion) {
        bitacora log = new bitacora();
        log.setIdPaciente(idPaciente);
        log.setAccion(accion);
        log.setFechaHora(java.time.LocalDateTime.now());
        bitacoraRepo.save(log);
    }
}

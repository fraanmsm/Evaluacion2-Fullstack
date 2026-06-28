package cl.duoc.api_triage.Service;

import cl.duoc.api_triage.Model.triage;
import cl.duoc.api_triage.Model.bitacora;
import cl.duoc.api_triage.Repository.triageRepository;
import cl.duoc.api_triage.Repository.bitacoraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class triageServiceTest {

    @Mock
    private triageRepository repository;

    @Mock
    private bitacoraRepository bitacoraRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private triageService service;

    private triage pacientePrueba;

    @BeforeEach
    void setUp() {
        // Datos frescos antes de cada test
        pacientePrueba = new triage();
        pacientePrueba.setId(1L);
        pacientePrueba.setNombre("Juan");
        pacientePrueba.setApellidoPaterno("Perez");
        pacientePrueba.setApellidoMaterno("Soto");
        pacientePrueba.setGravedad(3);
        pacientePrueba.setActivo(true);
    }

    @Test
    @DisplayName("Registrar paciente exitosamente (Camino Feliz)")
    void registrar_PacienteNuevo_Exito() {
        // Given (Arrange)
        when(repository.findByNombreAndApellidoPaternoAndApellidoMaterno(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty()); // Simulamos que NO existe duplicado

        when(restTemplate.getForObject(anyString(), eq(Boolean.class)))
                .thenReturn(true); // Simulamos que HAY camas disponibles

        when(repository.save(any(triage.class))).thenReturn(pacientePrueba);

        // When (Act)
        triage resultado = service.registrar(pacientePrueba);

        // Then (Assert)
        assertNotNull(resultado);
        assertEquals("EN ESPERA", pacientePrueba.getEstadoEspera());
        verify(repository).save(pacientePrueba);
        verify(bitacoraRepo).save(any(bitacora.class)); // Verificamos que se guarde la auditoría
    }

    @Test
    @DisplayName("Registrar falla si el paciente ya existe")
    void registrar_PacienteDuplicado_LanzaExcepcion() {
        // Given (Arrange)
        when(repository.findByNombreAndApellidoPaternoAndApellidoMaterno(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(pacientePrueba)); // Simulamos que SÍ existe

        // When & Then (Act & Assert)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.registrar(pacientePrueba);
        });

        assertTrue(exception.getMessage().contains("Ya existe un paciente con el nombre"));
        verify(repository, never()).save(any(triage.class)); // Nunca debe llegar a guardar
    }

    @Test
    @DisplayName("Dar de alta a un paciente NO crítico (Gravedad 3, 4, 5) exitosamente")
    void darDeAlta_GravedadNormal_Exito() {
        // Given (Arrange)
        when(repository.findById(1L)).thenReturn(Optional.of(pacientePrueba));

        // When (Act)
        service.darDeAlta(1L);

        // Then (Assert)
        assertFalse(pacientePrueba.isActivo()); // Debe pasar a false
        verify(repository).save(pacientePrueba);
        verify(bitacoraRepo).save(any(bitacora.class)); // Verificamos que se guarde la auditoría
    }

    @Test
    @DisplayName("Dar de alta falla si el paciente es Crítico (Gravedad 1 o 2)")
    void darDeAlta_GravedadCritica_LanzaExcepcion() {
        // Given (Arrange)
        pacientePrueba.setGravedad(1); // Modificamos a gravedad crítica
        when(repository.findById(1L)).thenReturn(Optional.of(pacientePrueba));

        // When & Then (Act & Assert)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.darDeAlta(1L);
        });

        assertEquals("No se puede dar de alta a un paciente con gravedad crítica (1 o 2)", exception.getMessage());
        verify(repository, never()).save(any(triage.class)); // Nunca debe guardar el cambio
        verify(bitacoraRepo, never()).save(any(bitacora.class)); // No debe registrar alta en bitácora
    }
}
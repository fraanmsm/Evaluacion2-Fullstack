package cl.duoc.api_triage.Repository;

import cl.duoc.api_triage.Model.triage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface triageRepository extends JpaRepository<triage, Long> {
    
    Optional<triage> findByNombreAndApellidoPaternoAndApellidoMaterno(String nombre, String apellidoPaterno, String apellidoMaterno);

    
    List<triage> findByGravedad(Integer gravedad);
}

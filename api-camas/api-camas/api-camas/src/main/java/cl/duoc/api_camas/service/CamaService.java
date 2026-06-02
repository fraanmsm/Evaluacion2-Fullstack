package cl.duoc.api_camas.service;

import cl.duoc.api_camas.model.TriageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CamaService {

    @Autowired
    private RestTemplate restTemplate;
    private final String URL_TRIAGE = "http://localhost:16800/api/v1/triage/criticos";

    public List<TriageDTO> obtenerListaCriticos() {

        TriageDTO[] respuesta = restTemplate.getForObject(URL_TRIAGE, TriageDTO[].class);
        return new ArrayList<>(Arrays.asList(respuesta));
    }

    public boolean verificarCamaPorGravedad(Integer gravedad) {
        if (gravedad == 1) {
            return false;
        }
        return true;
    }
}
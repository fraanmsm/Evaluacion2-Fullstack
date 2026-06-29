package cl.duoc.api_triage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI triageOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Módulo de Triage Clínico - Hospital Regional")
                        .description("Sistema de gestión, priorización de pacientes críticos y auditoría interna de contingencias.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("DuocUC - Equipo de Desarrollo")
                                .email("soporte@duoc.cl"))
                        .license(new License()
                                .name("Uso Académico"))
                );
    }
}

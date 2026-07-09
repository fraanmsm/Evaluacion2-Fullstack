package cl.duoc.api_triage.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue colaCamas() {
        return new Queue("cola-camas", false);
    }

}

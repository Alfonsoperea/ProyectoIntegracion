package aiss.videominer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI videoMinerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VideoMiner API")
                        .description("Microservicio principal para gestionar canales y videos.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipo Proyecto Integracion")
                                .email("equipo@example.com")));
    }
}

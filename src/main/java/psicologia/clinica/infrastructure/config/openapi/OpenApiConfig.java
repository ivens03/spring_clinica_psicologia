package psicologia.clinica.infrastructure.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clínica de Psicologia API")
                        .version("0.0.4")
                        .description("API para gestão de clínicas de psicologia - Foco em Segurança e Ética")
                        .contact(new Contact()
                                .name("Suporte Técnico")
                                .email("suporte@clinica.psicologia")));
    }
}

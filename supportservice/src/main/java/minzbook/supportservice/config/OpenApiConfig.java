package minzbook.supportservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MinzBook – Support Service API",
        version = "1.0.0",
        description = "Microservicio de soporte, tickets y ayuda al usuario de MinzBook.",
        contact = @Contact(
            name = "Equipo MinzBook",
            email = "soporte@minzbook.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8084", description = "Support Service – Local")
    }
)
public class OpenApiConfig {
}

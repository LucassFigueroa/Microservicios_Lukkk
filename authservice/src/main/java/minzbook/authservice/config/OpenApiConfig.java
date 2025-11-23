package minzbook.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MinzBook – Auth Service API",
        version = "1.0.0",
        description = "Microservicio de autenticación y gestión de usuarios de MinzBook.",
        contact = @Contact(
            name = "Equipo MinzBook",
            email = "soporte@minzbook.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Auth Service – Local")
    }
)
public class OpenApiConfig {
}

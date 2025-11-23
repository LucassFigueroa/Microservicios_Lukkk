package minzbook.catalogservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MinzBook – Catalog Service API",
        version = "1.0.0",
        description = "Microservicio de catálogo de libros y gestión de títulos de MinzBook.",
        contact = @Contact(
            name = "Equipo MinzBook",
            email = "soporte@minzbook.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8082", description = "Catalog Service – Local")
    }
)
public class OpenApiConfig {
}

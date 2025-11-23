package minzbook.authservice.config;

import minzbook.authservice.model.User;
import minzbook.authservice.model.Role;
import minzbook.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {

            // ADMIN
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                User admin = new User();
                admin.setEmail("admin@gmail.com");
                admin.setNombre("Admin");
                admin.setApellido("");
                admin.setRol(Role.ADMIN);
                admin.setActivo(true);
                admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
                userRepository.save(admin);
                System.out.println("[DataLoader] Creado admin@gmail.com");
            }

            // SOPORTE
            if (!userRepository.existsByEmail("soporte@gmail.com")) {
                User soporte = new User();
                soporte.setEmail("soporte@gmail.com");
                soporte.setNombre("Soporte");
                soporte.setApellido("");
                soporte.setRol(Role.SUPPORT);
                soporte.setActivo(true);
                soporte.setPasswordHash(passwordEncoder.encode("Soporte123!"));
                userRepository.save(soporte);
                System.out.println("[DataLoader] Creado soporte@gmail.com");
            }

            // USUARIO NORMAL
            if (!userRepository.existsByEmail("luc@gmail.com")) {
                User usuario = new User();
                usuario.setEmail("luc@gmail.com");
                usuario.setNombre("Lucas");
                usuario.setApellido("");
                usuario.setRol(Role.USER);
                usuario.setActivo(true);
                usuario.setPasswordHash(passwordEncoder.encode("Lucas123!"));
                userRepository.save(usuario);
                System.out.println("[DataLoader] Creado luc@gmail.com");
            }
        };
    }
}

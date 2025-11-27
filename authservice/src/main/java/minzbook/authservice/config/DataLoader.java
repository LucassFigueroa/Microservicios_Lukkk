package minzbook.authservice.config;

import minzbook.authservice.model.RoleName;
import minzbook.authservice.model.User;
import minzbook.authservice.model.Role;
import minzbook.authservice.repository.UserRepository;
import minzbook.authservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@DependsOn("entityManagerFactory")
public class DataLoader {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {

            // --- CREAR ROLES ---
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_ADMIN)));
            Role supportRole = roleRepository.findByName(RoleName.ROLE_SUPPORT).orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_SUPPORT)));
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_USER)));

            System.out.println("[DataLoader] Roles asegurados en la base de datos.");


            // --- CREAR USUARIOS ---

            // ADMIN
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                User admin = new User();
                admin.setEmail("admin@gmail.com");
                admin.setNombre("Admin");
                admin.setApellido("");
                admin.setRol(adminRole);
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
                soporte.setRol(supportRole);
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
                usuario.setRol(userRole);
                usuario.setActivo(true);
                usuario.setPasswordHash(passwordEncoder.encode("Lucas123!"));
                userRepository.save(usuario);
                System.out.println("[DataLoader] Creado luc@gmail.com");
            }
        };
    }
}

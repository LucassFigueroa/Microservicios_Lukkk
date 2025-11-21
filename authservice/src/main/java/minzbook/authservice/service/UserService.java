package minzbook.authservice.service;

import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserLoginRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.model.User;
import minzbook.authservice.model.Role;
import minzbook.authservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    @Value("${admin.secret}")
    private String adminSecret;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setRol(Role.USER);  

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse login(UserLoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        return toResponse(user);
    }

    public void assignRole(Long userId, String nuevoRol, String adminKey) {

        if (!adminKey.equals(adminSecret)) {
            throw new RuntimeException("No autorizado. Solo un ADMIN puede asignar roles.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    
        Role rolEnum;
        try {
            rolEnum = Role.valueOf(nuevoRol.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Rol inválido. Usa USER, SOPORTE o ADMIN.");
        }
        user.setRol(rolEnum);
        userRepository.save(user);
    }
    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setApellido(user.getApellido());
        dto.setRol(user.getRol().name()); 
        return dto;
    }
}

package minzbook.authservice.service;

import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserLoginRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.model.User;
import minzbook.authservice.model.Role;
import minzbook.authservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.secret}")
    private String adminSecret;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================
    // REGISTER
    // ============================
    public UserResponse register(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya está registrado"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setRol(Role.USER);
        user.setActivo(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // ============================
    // LOGIN
    // ============================
    public UserResponse login(UserLoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario o contraseña incorrectos"
                ));

        // OJO: tu campo activo es Boolean, así que usamos getActivo()
        if (user.getActivo() != null && !user.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario inactivo"
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o contraseña incorrectos"
            );
        }

        return toResponse(user);
    }

    // ============================
    // ASIGNAR ROL
    // ============================
    public void assignRole(Long userId, String nuevoRol, String adminKey) {

        if (!adminKey.equals(adminSecret)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No autorizado. Solo un ADMIN puede asignar roles."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        Role rolEnum;
        try {
            rolEnum = Role.valueOf(nuevoRol.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido. Usa USER, SUPPORT o ADMIN."
            );
        }

        user.setRol(rolEnum);
        userRepository.save(user);
    }

    // ============================
    // MAPEAR A DTO
    // ============================
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

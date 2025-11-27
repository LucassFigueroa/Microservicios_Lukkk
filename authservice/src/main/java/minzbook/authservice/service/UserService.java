package minzbook.authservice.service;

import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserLoginRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.model.Role;
import minzbook.authservice.model.RoleName;
import minzbook.authservice.model.User;
import minzbook.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import minzbook.authservice.repository.RoleRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol de usuario por defecto no existe."
                ));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setRol(userRole);
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
    public void assignRole(Long userId, String nuevoRol) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        RoleName roleNameEnum;
        try {
            // Construimos el nombre completo del rol, ej: "ROLE_ADMIN" a partir de "admin"
            roleNameEnum = RoleName.valueOf("ROLE_" + nuevoRol.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido. Usa USER, SUPPORT o ADMIN."
            );
        }
        Role role = roleRepository.findByName(roleNameEnum)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "El rol " + nuevoRol.toUpperCase() + " no está configurado en la base de datos."
                ));
        user.setRol(role);
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
        dto.setRol(user.getRol().getName().name());
        return dto;
    }
}

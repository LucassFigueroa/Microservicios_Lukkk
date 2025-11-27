package minzbook.authservice.controller;

import minzbook.authservice.dto.MessageResponse;
import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.AssignRoleRequest;
import minzbook.authservice.dto.UserLoginRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ===== Register =====
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    // ===== Login =====
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ===== Health Check =====
    @GetMapping("/health")
    public String health() {
        return "Auth service OK";
    }

    // ===== Solo ADMIN puede asignar roles =====
    @PatchMapping("/role/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden acceder
    public ResponseEntity<MessageResponse> assignRole(
            @PathVariable Long userId,
            @RequestBody AssignRoleRequest request
    ) {
        userService.assignRole(userId, request.getRol());
        return ResponseEntity.ok(new MessageResponse("Rol actualizado correctamente para el usuario " + userId));
    }
}

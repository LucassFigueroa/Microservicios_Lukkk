package minzbook.authservice.service;

import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.model.Role;
import minzbook.authservice.model.User;
import minzbook.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_creaUsuarioCuandoEmailNoExiste() {
        // --- Arrange ---
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNombre("Lucas");
        request.setApellido("Figueroa");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        // Solo este test necesita el encode:
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setNombre("Lucas");
        savedUser.setApellido("Figueroa");
        savedUser.setRol(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // --- Act ---
        UserResponse response = userService.register(request);

        // --- Assert ---
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Lucas", response.getNombre());
        assertEquals("Figueroa", response.getApellido());
        assertEquals("USER", response.getRol());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User userToSave = captor.getValue();

        assertEquals("hashed-password", userToSave.getPasswordHash());
    }

    @Test
    void register_lanzaExcepcionCuandoEmailYaExiste() {
        // --- Arrange ---
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("yaexiste@example.com");
        request.setPassword("password123");

        User existing = new User();
        existing.setEmail("yaexiste@example.com");

        when(userRepository.findByEmail("yaexiste@example.com"))
                .thenReturn(Optional.of(existing));

        // --- Act + Assert ---
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.register(request)
        );

        assertNotNull(ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
        // acá NO stubbeamos ni verificamos passwordEncoder
    }
}

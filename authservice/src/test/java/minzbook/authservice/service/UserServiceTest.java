package minzbook.authservice.service;

import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserLoginRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.model.RoleName;
import minzbook.authservice.model.Role;
import minzbook.authservice.model.User;
import minzbook.authservice.repository.RoleRepository;
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
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // -------------------------------------------------------
    //  REGISTER
    // -------------------------------------------------------

    @Test
    void register_creaUsuarioCuandoEmailNoExiste() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNombre("Lucas");
        request.setApellido("Figueroa");

        Role userRole = new Role(1L, RoleName.ROLE_USER);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");
        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(userRole));

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setNombre("Lucas");
        savedUser.setApellido("Figueroa");
        savedUser.setRol(userRole); // El rol asignado


        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Lucas", response.getNombre());
        assertEquals("Figueroa", response.getApellido());
        assertEquals("ROLE_USER", response.getRol());

        // se encripta la password
        verify(passwordEncoder).encode("password123");

        // capturamos el user guardado
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User userToSave = captor.getValue();

        assertEquals("test@example.com", userToSave.getEmail());
        assertEquals("Lucas", userToSave.getNombre());
        assertEquals("Figueroa", userToSave.getApellido());
        assertEquals("hashed-password", userToSave.getPasswordHash());
    }

    @Test
    void register_lanzaExcepcionCuandoEmailYaExiste() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("yaexiste@example.com");
        request.setPassword("password123");

        User existing = new User();
        existing.setEmail("yaexiste@example.com");

        when(userRepository.findByEmail("yaexiste@example.com"))
                .thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.register(request)
        );

        assertNotNull(ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_asignaRolUserPorDefecto() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("rol@test.com");
        request.setPassword("pass");
        request.setNombre("Rol");
        request.setApellido("User");

        Role userRole = new Role(1L, RoleName.ROLE_USER);

        when(userRepository.findByEmail("rol@test.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass"))
                .thenReturn("hashed-pass");
        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(userRole));

        User saved = new User();
        saved.setEmail("rol@test.com");
        saved.setNombre("Rol");
        saved.setApellido("User");
        saved.setRol(userRole);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.register(request);

        assertEquals("ROLE_USER", response.getRol());
    }

    // -------------------------------------------------------
    //  LOGIN
    // -------------------------------------------------------

    @Test
    void login_retornaUsuarioCuandoCredencialesSonValidas() {
        UserLoginRequest req = new UserLoginRequest();
        req.setEmail("login@test.com");
        req.setPassword("1234");

        Role userRole = new Role(1L, RoleName.ROLE_USER);
        User user = new User();
        user.setEmail("login@test.com");
        user.setPasswordHash("hashed-pass");
        user.setNombre("Lucas");        user.setApellido("Fig");
        user.setRol(userRole);

        when(userRepository.findByEmail("login@test.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "hashed-pass"))
                .thenReturn(true);

        UserResponse res = userService.login(req);

        assertNotNull(res);
        assertEquals("login@test.com", res.getEmail());
        assertEquals("Lucas", res.getNombre());
        assertEquals("Fig", res.getApellido());
        assertEquals("ROLE_USER", res.getRol());
    }

    @Test
    void login_lanzaExcepcionCuandoUsuarioNoExiste() {
        UserLoginRequest req = new UserLoginRequest();
        req.setEmail("noexiste@test.com");
        req.setPassword("1234");

        when(userRepository.findByEmail("noexiste@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.login(req)
        );

        assertNotNull(ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_lanzaExcepcionCuandoPasswordEsIncorrecta() {
        UserLoginRequest req = new UserLoginRequest();
        req.setEmail("login@test.com");
        req.setPassword("mala");

        User user = new User();
        user.setEmail("login@test.com");
        user.setPasswordHash("hashed-pass");

        when(userRepository.findByEmail("login@test.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mala", "hashed-pass"))
                .thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.login(req)
        );

        assertNotNull(ex.getMessage());
    }

    // -------------------------------------------------------
    //  ASSIGN ROLE
    // -------------------------------------------------------

    @Test
    void assignRole_actualizaRolCorrectamente() {
        // given
        Long userId = 1L;
        String nuevoRolString = "ADMIN";

        Role userRole = new Role(1L, RoleName.ROLE_USER);
        Role adminRole = new Role(2L, RoleName.ROLE_ADMIN);

        User user = new User();
        user.setId(userId);
        user.setRol(userRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        // when
        userService.assignRole(userId, nuevoRolString);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(RoleName.ROLE_ADMIN, saved.getRol().getName());
    }

    @Test
    void assignRole_lanzaExcepcionCuandoUsuarioNoExiste() {
        // given
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when + then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.assignRole(userId, "ADMIN")
        );

        assertNotNull(ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}

package minzbook.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import minzbook.authservice.dto.UserRegisterRequest;
import minzbook.authservice.dto.UserResponse;
import minzbook.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva seguridad
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void register_retorna201ConUserResponse() throws Exception {

        UserRegisterRequest req = new UserRegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("pass123");
        req.setNombre("Lucas");
        req.setApellido("Fig");

        UserResponse res = new UserResponse();
        res.setEmail("test@example.com");
        res.setNombre("Lucas");
        res.setApellido("Fig");
        res.setRol("USER");

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(res);

        mockMvc.perform(
                post("/auth/register")   // ← ESTA ES TU RUTA REAL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isCreated())        // 201
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nombre").value("Lucas"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }
}

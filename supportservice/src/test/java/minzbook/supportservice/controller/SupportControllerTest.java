package minzbook.supportservice.controller;

import minzbook.supportservice.model.Conversation;
import minzbook.supportservice.service.SupportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupportController.class)
@AutoConfigureMockMvc(addFilters = false)  // ❗ Desactiva seguridad
class SupportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportService supportService;

    @Test
    void health_debeResponderOk() throws Exception {
        mockMvc.perform(get("/support/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Support service OK"));
    }

    @Test
    void getAllConversations_debeRetornarListaDeConversaciones() throws Exception {
        // Mock de datos
        Conversation conv = new Conversation();
        conv.setId(1L);
        conv.setAsunto("Problema con el login");

        List<Conversation> conversations = Collections.singletonList(conv);

        // Simular comportamiento del service
        given(supportService.getAllConversations()).willReturn(conversations);

        // Ejecutar request y validar
        mockMvc.perform(get("/support/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].asunto").value("Problema con el login"));

        verify(supportService).getAllConversations();
    }
}

package minzbook.supportservice.service;

import minzbook.supportservice.model.Conversation;
import minzbook.supportservice.model.ConversationStatus;
import minzbook.supportservice.repository.ConversationRepository;
import minzbook.supportservice.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private SupportService supportService;

    @Test
    void closeConversation_debeCerrarLaConversacionYCambiarFechaActualizacion() {
        // Given
        Long conversationId = 1L;

        Conversation existing = new Conversation();
        existing.setId(conversationId);
        existing.setStatus(ConversationStatus.OPEN);
        LocalDateTime oldFechaActualizacion = LocalDateTime.of(2024, 1, 1, 0, 0);
        existing.setFechaActualizacion(oldFechaActualizacion);

        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.of(existing));

        // Para que save devuelva el mismo objeto que recibe (patrón típico en tests)
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Conversation result = supportService.closeConversation(conversationId);

        // Then
        assertNotNull(result, "La conversación resultante no debería ser null");
        assertEquals(ConversationStatus.CLOSED, result.getStatus(),
                "El estado de la conversación debería quedar en CLOSED");
        assertNotNull(result.getFechaActualizacion(),
                "La fecha de actualización no debería ser null");

        // si tu lógica la cambia a now(), debería ser posterior a la anterior
        assertTrue(result.getFechaActualizacion().isAfter(oldFechaActualizacion),
                "La fecha de actualización debería ser posterior a la anterior");

        verify(conversationRepository).findById(conversationId);
        verify(conversationRepository).save(result);
        verifyNoInteractions(messageRepository);
    }

    @Test
    void closeConversation_siNoExisteLanzaExcepcion() {
        // Given
        Long conversationId = 99L;
        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.empty());

        // When + Then
        // Asumo que tu método getConversation lanza RuntimeException si no encuentra la conversación
        assertThrows(RuntimeException.class,
                () -> supportService.closeConversation(conversationId),
                "Debería lanzar una excepción cuando la conversación no existe");

        verify(conversationRepository).findById(conversationId);
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void getAllConversations_debeRetornarListaDesdeRepositorio() {
        // Given
        Conversation c1 = new Conversation();
        c1.setId(1L);
        c1.setAsunto("Asunto 1");

        Conversation c2 = new Conversation();
        c2.setId(2L);
        c2.setAsunto("Asunto 2");

        List<Conversation> lista = Arrays.asList(c1, c2);
        when(conversationRepository.findAll()).thenReturn(lista);

        // When
        List<Conversation> result = supportService.getAllConversations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Asunto 1", result.get(0).getAsunto());

        verify(conversationRepository).findAll();
        verifyNoMoreInteractions(conversationRepository);
        verifyNoInteractions(messageRepository);
    }
}

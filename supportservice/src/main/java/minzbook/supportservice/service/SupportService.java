package minzbook.supportservice.service;

import minzbook.supportservice.dto.CreateConversationRequest;
import minzbook.supportservice.dto.SendMessageRequest;
import minzbook.supportservice.model.*;
import minzbook.supportservice.repository.ConversationRepository;
import minzbook.supportservice.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public SupportService(ConversationRepository conversationRepository,
                          MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // Crear conversación con primer mensaje
    public Conversation createConversation(CreateConversationRequest request) {
        Conversation conv = new Conversation();
        conv.setUserId(request.getUserId());
        conv.setAsunto(request.getAsunto());
        conv.setStatus(ConversationStatus.OPEN);
        conv.setFechaCreacion(LocalDateTime.now());
        conv.setFechaActualizacion(LocalDateTime.now());

        Conversation saved = conversationRepository.save(conv);

        // mensaje inicial
        Message msg = new Message();
        msg.setConversation(saved);
        msg.setUserId(request.getUserId());
        msg.setContenido(request.getMensajeInicial());
        msg.setFechaEnvio(LocalDateTime.now());
        messageRepository.save(msg);

        return saved;
    }

    // Obtener conversaciones del usuario
    public List<Conversation> getConversationsByUser(Long userId) {
        return conversationRepository.findByUserId(userId);
    }

    // Obtener una conversación
    public Conversation getConversation(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
    }

    // Enviar mensaje
    public Message sendMessage(SendMessageRequest request) {
        Conversation conv = getConversation(request.getConversationId());

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setUserId(request.getUserId());
        msg.setContenido(request.getContenido());
        msg.setFechaEnvio(LocalDateTime.now());

        conv.setFechaActualizacion(LocalDateTime.now());
        conversationRepository.save(conv);

        return messageRepository.save(msg);
    }

    // Obtener mensajes de una conversación
    public List<Message> getMessagesByConversation(Long conversationId) {
        Conversation conv = getConversation(conversationId);
        return messageRepository.findByConversationOrderByFechaEnvioAsc(conv);
    }

    // Cerrar conversación
    public Conversation closeConversation(Long id) {
        Conversation conv = getConversation(id);
        conv.setStatus(ConversationStatus.CLOSED);
        conv.setFechaActualizacion(LocalDateTime.now());
        return conversationRepository.save(conv);
    }
    public List<Conversation> getAllConversations() {
    return conversationRepository.findAll();
}

}

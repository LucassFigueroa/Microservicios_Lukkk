package minzbook.supportservice.controller;

import jakarta.validation.Valid;
import minzbook.supportservice.dto.CreateConversationRequest;
import minzbook.supportservice.dto.SendMessageRequest;
import minzbook.supportservice.model.Conversation;
import minzbook.supportservice.model.Message;
import minzbook.supportservice.service.SupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support")
@CrossOrigin(origins = "*")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        return ResponseEntity.ok(supportService.createConversation(request));
    }

    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<Conversation>> getConversationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(supportService.getConversationsByUser(userId));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getConversation(id));
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(supportService.sendMessage(request));
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(supportService.getMessagesByConversation(conversationId));
    }

    @PatchMapping("/conversations/{id}/close")
    public ResponseEntity<Conversation> closeConversation(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.closeConversation(id));
    }

    @GetMapping("/health")
    public String health() {
        return "Support service OK";
    }
    @GetMapping("/conversations")
public ResponseEntity<List<Conversation>> getAllConversations() {
    return ResponseEntity.ok(supportService.getAllConversations());
}

}

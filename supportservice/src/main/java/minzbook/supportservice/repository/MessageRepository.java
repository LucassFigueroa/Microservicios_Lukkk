package minzbook.supportservice.repository;

import minzbook.supportservice.model.Message;
import minzbook.supportservice.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByFechaEnvioAsc(Conversation conversation);
}

package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.server.domain.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomId(Long roomId);
}

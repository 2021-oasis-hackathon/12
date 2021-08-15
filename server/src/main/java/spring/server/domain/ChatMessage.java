package spring.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

@Getter @Setter
public class ChatMessage {
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

}

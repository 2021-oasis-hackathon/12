package spring.server.domain;

import lombok.Getter;
import lombok.Setter;
import spring.server.dto.UserDTO;

import javax.persistence.*;

@Getter @Setter
@Entity
public class ChatMessage {
    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
    private MessageType type;
    private Long roomId;
    private Long senderId;

    @Embedded
    private UserDTO user;
    private String message;

}

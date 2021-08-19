package spring.server.domain;

import lombok.Getter;
import lombok.Setter;
import spring.server.embeddable.UserInfo;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime createTime;

    @Embedded
    private UserInfo user;
    private String message;

}

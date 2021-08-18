package spring.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.*;

@Entity
@Getter @Setter
public class UserChatRoom {
    @Id
    @GeneratedValue
    @Column(name = "user_chatRoom_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "boolean default false")
    private boolean isChecked;

    private Long otherId;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    public UserChatRoom() {
    }
    public static UserChatRoom create(User user, Long otherId) {
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.user = user;
        userChatRoom.otherId = otherId;
        return userChatRoom;
    }
}

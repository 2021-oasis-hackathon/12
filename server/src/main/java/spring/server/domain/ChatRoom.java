package spring.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
public class ChatRoom {
    @Id @GeneratedValue
    @Column(name = "chatRoom_id")
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    public static ChatRoom create(String name, UserChatRoom userChatRoom1, UserChatRoom userChatRoom2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.userChatRooms.add(userChatRoom1);
        chatRoom.userChatRooms.add(userChatRoom2);
        userChatRoom1.setChatRoom(chatRoom);
        userChatRoom2.setChatRoom(chatRoom);
        return chatRoom;
    }

}


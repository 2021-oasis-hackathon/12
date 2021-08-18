package spring.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime renewalTime;
    private String renewalMessage;

    public static ChatRoom create(String name, UserChatRoom userChatRoom1, UserChatRoom userChatRoom2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.userChatRooms.add(userChatRoom1);
        chatRoom.userChatRooms.add(userChatRoom2);
        userChatRoom1.setChatRoom(chatRoom);
        userChatRoom2.setChatRoom(chatRoom);
        chatRoom.renewalTime = LocalDateTime.now();
        return chatRoom;
    }

    public String getTime() {
        Period between = Period.between(renewalTime.toLocalDate(), LocalDate.now());
        if (between.isZero()) {
            return renewalTime.format(DateTimeFormatter.ofPattern("a hh:mm"));
        }
        return between.getDays() + "d";
    }

    public boolean getChecked(Long userId) {
        for (UserChatRoom u : userChatRooms) {
           if(u.getUser().getId() == userId) {
              return u.isChecked();
           }
        }
        return false;
    }

}


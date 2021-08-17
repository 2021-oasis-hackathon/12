package spring.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import spring.server.domain.ChatRoom;
import spring.server.domain.User;
import spring.server.domain.UserChatRoom;
import spring.server.repository.ChatRoomRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(User user, User host) {
        UserChatRoom userChatRoom1 = UserChatRoom.create(user, host.getId());
        UserChatRoom userChatRoom2 = UserChatRoom.create(host, user.getId());
        ChatRoom chatRoom = ChatRoom.create(user.getNickname() + ", " + host.getNickname(),
                userChatRoom1, userChatRoom2);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return savedChatRoom;
    }

    public List<ChatRoom> findRoomByUserId(Long userId) {
        return chatRoomRepository.findRoomByUserId(userId);
    }

    public ChatRoom findRoomByUsers(Long userId, Long hostId) {
        return chatRoomRepository.findRoomByUsers(userId, hostId);
    }
}



package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.server.api.ApiMessage;
import spring.server.domain.ChatMessage;
import spring.server.domain.ChatRoom;
import spring.server.domain.User;
import spring.server.domain.UserChatRoom;
import spring.server.dto.CreateChatRoomDTO;
import spring.server.dto.ReceiveCheckDTO;
import spring.server.dto.SuccessDTO;
import spring.server.repository.ChatMessageRepository;
import spring.server.repository.UserChatRoomRepository;
import spring.server.service.ChatService;
import spring.server.service.UserService;
import spring.server.token.JwtToken;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    @GetMapping("/rooms/dispatcher")
    public String dispatcher(@RequestParam("token") String token, Model model) {
        String username = (String) JwtToken.parseJwtToken(token).get("username");
        Long userId = userService.findByUsername(username).getId();
        return "redirect:/chat/rooms/" + userId;
    }
    // 채팅 리스트 화면
    @GetMapping("/rooms/{userId}")
    public String rooms(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("list", chatService.findRoomByUserId(userId));
        return "/chat/rooms";
    }
    // 채팅방 생성
    @GetMapping("/room")
    public String createRoom(@RequestParam("token") String token, @RequestParam("hostId") Long hostId, RedirectAttributes redirectAttributes) {
        String username = (String) JwtToken.parseJwtToken(token).get("username");
        Long userId = userService.findByUsername(username).getId();
        ChatRoom roomByUsers = chatService.findRoomByUsers(userId, hostId);
        User user = userService.findById(userId).orElseThrow(RuntimeException::new);
        Long roomId;
        if (roomByUsers != null) {
            roomId = roomByUsers.getId();
        } else {
            User host = userService.findById(hostId).orElseThrow(RuntimeException::new);
            ChatRoom chatRoom = chatService.createChatRoom(user, host);
            roomId = chatRoom.getId();
        }
        redirectAttributes.addAttribute("userId" , userId);

        return "redirect:/chat/room/enter?id=" + roomId;
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter")
    public String roomDetail(Model model, @RequestParam("id") Long roomId
            , @RequestParam("userId") Long userId) {
        userChatRoomRepository.updateCheckTrue(roomId, userId);
        List<ChatMessage> messages = chatMessageRepository.findByRoomId(roomId);
        User user = userService.findById(userId).orElseThrow(RuntimeException::new);
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        model.addAttribute("roomId", roomId);
            return "/chat/room";
    }

    @PostMapping("/receive")
    public ResponseEntity<ApiMessage> receiveMessage(@RequestBody ReceiveCheckDTO receiveCheckDTO) {
        userChatRoomRepository.updateCheckTrue(receiveCheckDTO.getRoomId(), receiveCheckDTO.getUserId());
        return ResponseEntity.ok(ApiMessage.builder().data(new SuccessDTO(true, "success")).build());
    }
}

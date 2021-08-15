package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.server.domain.ChatRoom;
import spring.server.domain.User;
import spring.server.service.ChatService;
import spring.server.service.UserService;
import spring.server.token.JwtToken;


@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;
    private final UserService userService;

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
    @PostMapping("/room")
    public String createRoom(@RequestParam("userId") Long userId
            , @RequestParam("hostId") Long hostId, RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addAttribute("user" , user);

        return "redirect:/chat/room/enter?id=" + roomId;
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter")
    public String roomDetail(Model model, @RequestParam("id") Long roomId
            , @RequestParam("user") User user) {
//        chatService.findMessageByRoomId(roomId);
//        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        return "/chat/room";
    }
}

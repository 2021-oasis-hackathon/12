package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import spring.server.api.ApiMessage;
import spring.server.domain.Locker;
import spring.server.domain.User;
import spring.server.dto.LockerDTO;
import spring.server.service.LockerService;
import spring.server.service.UserService;
import spring.server.token.JwtToken;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/locker")
@RequiredArgsConstructor
public class LockerController {
    private final LockerService lockerService;
    private final SmartValidator smartValidator;
    private final UserService userService;
    @GetMapping("/register/{userId}")
    public String register(@PathVariable("userId") Long userId, Model model) {
        User user = new User();
        user.setId(userId);
        Locker locker = new Locker();
        locker.setUser(user);
        model.addAttribute("locker",locker);
        return "/locker/registerForm";
    }

    @PostMapping("/register")
    public String registerProcess(@ModelAttribute Locker locker, BindingResult result) {
        smartValidator.validate(locker, result);
        if (result.hasErrors()) {
            return "/locker/registerForm";
        }
        lockerService.create(locker);
        return "redirect:/mypage/" + locker.getUser().getId();
    }

    @GetMapping("/get")
    public ResponseEntity<ApiMessage> getLockers() {
//        List<Locker> lockers = lockerService.getLockers(user.getId());
        List<Locker> lockers = lockerService.findAll();
        List<LockerDTO> collect = lockers.stream().map(locker -> locker.getLockerDTO())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiMessage.builder().data(collect).build());
    }
}

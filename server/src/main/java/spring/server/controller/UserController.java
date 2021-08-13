package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import spring.server.api.ApiMessage;
import spring.server.domain.User;
import spring.server.dto.LoginDTO;
import spring.server.service.UserService;
import spring.server.validator.UserValidator;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmartValidator smartValidator;
    private final UserValidator userValidator;

    @GetMapping("/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute User user, BindingResult result) {
        smartValidator.validate(user, result);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "user/signup";
        }
        userService.create(user);
        return "redirect:/login";
    }

    @PostMapping("/api/login")
    public ResponseEntity<ApiMessage> loginProcess(@RequestBody User user) {
        LoginDTO loginInfo = userService.login(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(ApiMessage.builder().data(loginInfo).message(loginInfo.getMessage()).build());
    }

}

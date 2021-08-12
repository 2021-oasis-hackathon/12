package spring.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import spring.server.domain.User;
import spring.server.service.UserService;
import spring.server.validator.UserValidator;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmartValidator smartValidator;
    private final UserValidator userValidator;

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute("user") User user, BindingResult result) {
        smartValidator.validate(user, result);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "user/signup";
        }
        userService.create(user);
        return "redirect:/login";
    }

}

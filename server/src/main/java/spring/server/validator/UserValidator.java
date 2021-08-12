package spring.server.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import spring.server.domain.User;
import spring.server.service.UserService;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {
    private final UserService userService;
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", null, "이미 존재하는 사용자입니다.");
        }
    }
}

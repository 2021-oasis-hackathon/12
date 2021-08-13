package spring.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.server.domain.User;
import spring.server.dto.LoginDTO;
import spring.server.repository.UserRepository;
import spring.server.token.JwtToken;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginDTO login(String username, String password) {
        User findUser = userRepository.findByUsername(username);

        if (findUser == null) {
            return new LoginDTO(false, "해당하는 아이디의 유저를 찾을 수 없습니다.", null);
        }
        if (passwordEncoder.matches(password, findUser.getPassword())) {
            String token = JwtToken.makeJwtToken(findUser.getUsername());
            findUser.setToken(token);
            userRepository.save(findUser);
            return new LoginDTO(true, "로그인 성공", token);
        } else {
            return new LoginDTO(false, "비밀번호가 일치하지 않습니다.", null);
        }
    }

    public void create(User user) {
        user.passwordEncoding(passwordEncoder);
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginDTO {
    private boolean isSuccess;
    private String message;
    private String token;
}

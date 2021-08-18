package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.server.domain.Locker;

import javax.persistence.Embeddable;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
}

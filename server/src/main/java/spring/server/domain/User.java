package spring.server.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter @Setter
public class User {
    @Id @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String username; //login id
    @NotEmpty
    private String password;
    private String token;


    public void passwordEncoding(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}

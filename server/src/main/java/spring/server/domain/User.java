package spring.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.server.dto.UserDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String username; //login id
    @NotEmpty
    private String nickname;
    @NotEmpty
    private String password;
    private String email;
    private String phoneNumber;
    private String token;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Locker> lockers = new ArrayList<>(); //내 보관소

    @ManyToOne
    @JoinColumn(name = "entrust_locker_id")
    private Locker entrustLocker;   //내가 짐을 맡긴 보관소
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime entrustTime;

    public void passwordEncoding(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public UserDTO getUserDTO() {
        return new UserDTO(this.getId(), this.getUsername(), this.getNickname());
    }

    public void entrust(Locker locker) {
        this.setEntrustLocker(locker);
        locker.addCustomer(this);
    }

    public String getLockerUsedTime() {
        Duration duration = Duration.between(entrustTime, LocalDateTime.now());
        long minute = duration.getSeconds() / 60;
        long hour = duration.getSeconds() / 3600;
        String result = (hour < 10) ? "0" + hour : String.valueOf(hour);
        result += (minute < 10) ? "0" + minute : minute;
        return result;
    }
}

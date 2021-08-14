package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spring.server.domain.User;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class LockerDTO {
    private String lockerName;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private Long userId;
}

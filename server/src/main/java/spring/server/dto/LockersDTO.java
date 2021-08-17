package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class LockersDTO {
    private boolean isEntrust;
    private List<LockerDTO> lockers;
}

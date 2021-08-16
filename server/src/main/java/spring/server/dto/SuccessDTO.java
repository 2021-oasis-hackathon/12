package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SuccessDTO {
    private boolean isSuccess;
    private String message;
}

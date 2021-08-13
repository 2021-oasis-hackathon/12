package spring.server.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ApiMessage {
    public String message;
    public Object data;
}

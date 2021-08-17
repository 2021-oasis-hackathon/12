package spring.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntrustDTO {
    private String token;
    private String qrcode;
    private boolean isEntrust;
}

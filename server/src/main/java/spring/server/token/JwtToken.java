package spring.server.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class JwtToken {
    @Getter
    public static class Token {
        private String token;
    }
    public static String makeJwtToken(String username) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("granola")
                .setIssuedAt(now)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

    public static Claims parseJwtToken(String token) {

        return Jwts.parser()
                .setSigningKey("secret") // (3)
                .parseClaimsJws(token) // (4)
                .getBody();
    }
}

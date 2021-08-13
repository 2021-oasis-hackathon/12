package spring.server.token;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtToken {

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
}

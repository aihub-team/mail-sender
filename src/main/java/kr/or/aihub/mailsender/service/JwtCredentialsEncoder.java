package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * Jwt Credentials 암호화 담당.
 */
@Component
public class JwtCredentialsEncoder {
    private Key key;

    public JwtCredentialsEncoder(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 암호화된 Jwt Credentials를 리턴합니다.
     *
     * @param username 클레임에 포함될 유저 이름
     * @return 암호화된 Jwt Credentials
     */
    public String encode(String username) {
        return Jwts.builder()
                .claim("username", username)
                .signWith(key)
                .compact();
    }
}

package kr.or.aihub.mailsender.global.utils.application;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * Jwt Credential 암호화 담당.
 */
@Component
public class JwtCredentialEncoder {
    private final Key key;

    public JwtCredentialEncoder(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 암호화된 Jwt 자격증명을 리턴합니다.
     *
     * @param userId 유저 이름
     * @return 암호화된 Jwt 자격증명
     */
    public String encode(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .signWith(key)
                .compact();
    }
}

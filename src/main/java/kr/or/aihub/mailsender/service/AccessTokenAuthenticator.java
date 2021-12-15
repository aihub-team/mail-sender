package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

/**
 * 액세스 토큰 인증 담당.
 */
@Service
public class AccessTokenAuthenticator {
    private Key key;

    public AccessTokenAuthenticator(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 액세스 토큰을 해석하여 올바르면 true, 그렇지 않다면 false를 리턴합니다.
     *
     * @param accessToken 액세스 토큰
     * @return 액세스 토큰이 올바르면 true, 그렇지 않다면 false
     */
    public boolean authenticate(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SignatureException e) {
            return false;
        }

        return true;
    }
}

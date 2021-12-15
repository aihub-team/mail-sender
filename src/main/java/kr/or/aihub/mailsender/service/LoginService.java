package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;

/**
 * 로그인 처리 담당.
 */
@Service
public class LoginService {
    private Key key;

    public LoginService(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 클레임을 포함한 액세스 토큰을 리턴합니다.
     *
     * @param username 클레임에 포함될 유저이름
     * @return 클레임을 포함한 액세스 토큰
     */
    @Transactional
    public String login(String username) {
        return Jwts.builder()
                .claim("username", username)
                .signWith(key)
                .compact();
    }
}

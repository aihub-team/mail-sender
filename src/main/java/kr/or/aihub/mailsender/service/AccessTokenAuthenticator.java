package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.errors.EmptyAccessTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

/**
 * 액세스 토큰 인증 담당.
 */
@Service
public class AccessTokenAuthenticator {
    private final Key key;

    public AccessTokenAuthenticator(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 액세스 토큰을 인증합니다.
     *
     * @param accessToken 액세스 토큰
     * @throws EmptyAccessTokenException 토큰이 존재하지 않을 경우
     * @throws MalformedJwtException     토큰 형식이 올바르지 않을 경우
     * @throws SignatureException        토큰 서명이 실패한 경우
     */
    public void authenticate(String accessToken) {
        checkEmpty(accessToken);

        checkValid(accessToken);
    }

    /**
     * 올바른지 확인합니다.
     *
     * @param accessToken 액세스 토큰
     * @throws MalformedJwtException 토큰 형식이 올바르지 않을 경우
     * @throws SignatureException    토큰 서명이 실패한 경우
     */
    private void checkValid(String accessToken) {
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken);
    }

    /**
     * 비어있는지 확인합니다.
     *
     * @param accessToken 액세스 토큰
     * @throws EmptyAccessTokenException 액세스 토큰이 비어있을 경우
     */
    private void checkEmpty(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new EmptyAccessTokenException();
        }
    }
}

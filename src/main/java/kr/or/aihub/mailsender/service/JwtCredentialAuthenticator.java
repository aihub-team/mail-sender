package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.errors.EmptyJwtCredentialException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

/**
 * Jwt Credential 인증 담당.
 */
@Service
public class JwtCredentialAuthenticator {
    private final Key key;

    public JwtCredentialAuthenticator(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Jwt Credential를 인증합니다.
     *
     * @param jwtCredentials Jwt Credential
     * @throws EmptyJwtCredentialException Jwt Credential가 존재하지 않을 경우
     * @throws MalformedJwtException     Jwt Credential 형식이 올바르지 않을 경우
     * @throws SignatureException        Jwt Credential 서명이 실패한 경우
     */
    public void authenticate(String jwtCredentials) {
        checkEmpty(jwtCredentials);

        checkValid(jwtCredentials);
    }

    /**
     * 올바른지 확인합니다.
     *
     * @param jwtCredentials Jwt Credential
     * @throws MalformedJwtException Jwt Credential 형식이 올바르지 않을 경우
     * @throws SignatureException    Jwt Credential 서명이 실패한 경우
     */
    private void checkValid(String jwtCredentials) {
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtCredentials);
    }

    /**
     * 비어있는지 확인합니다.
     *
     * @param jwtCredentials Jwt Credential
     * @throws EmptyJwtCredentialException Jwt Credential이 비어있을 경우
     */
    private void checkEmpty(String jwtCredentials) {
        if (jwtCredentials == null || jwtCredentials.isBlank()) {
            throw new EmptyJwtCredentialException();
        }
    }
}

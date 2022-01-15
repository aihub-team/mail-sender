package kr.or.aihub.mailsender.global.utils.application;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.global.config.security.error.EmptyJwtCredentialException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

/**
 * Jwt Credential 복호화 담당.
 */
@Service
public class JwtCredentialDecoder {
    private final Key key;

    public JwtCredentialDecoder(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Jwt Credential에서 복호화된 정보를 리턴합니다.
     *
     * @param jwtCredential 암호화된 Jwt Credential
     * @param key           복호화할 페이로드의 키
     * @return key 값으로 찾은 복호화된 페이로드 값
     * @throws EmptyJwtCredentialException Jwt Credential가 존재하지 않을 경우
     * @throws MalformedJwtException       Jwt Credential 형식이 올바르지 않을 경우
     * @throws SignatureException          Jwt Credential 서명이 실패한 경우
     */
    public Object decode(String jwtCredential, String key) {
        checkEmpty(jwtCredential);

        JwtParser jwtParser = Jwts.parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer(Maps.of(
                        "userId", Long.class
                ).build()))
                .setSigningKey(this.key)
                .build();

        return jwtParser
                .parseClaimsJws(jwtCredential)
                .getBody()
                .get(key);
    }

    /**
     * 비어있는지 확인합니다.
     *
     * @param jwtCredential Jwt Credential
     * @throws EmptyJwtCredentialException Jwt Credential이 비어있을 경우
     */
    private void checkEmpty(String jwtCredential) {
        if (jwtCredential == null || jwtCredential.isBlank()) {
            throw new EmptyJwtCredentialException();
        }
    }
}

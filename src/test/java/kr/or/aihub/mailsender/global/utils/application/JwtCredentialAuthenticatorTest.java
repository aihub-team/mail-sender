package kr.or.aihub.mailsender.global.utils.application;

import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.global.config.security.error.EmptyJwtCredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtCredentialAuthenticatorTest {
    private static final String VALID_JWT_CREDENTIAL = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    @Autowired
    private JwtCredentialAuthenticator jwtCredentialAuthenticator;

    @Nested
    @DisplayName("authenticate 메서드는")
    class Describe_authenticate {

        @Nested
        @DisplayName("올바른 Jwt 자격 증명이 주어질 경우")
        class Context_validToken {
            private String validJwtCredential;

            @BeforeEach
            void setUp() {
                validJwtCredential = VALID_JWT_CREDENTIAL;
            }

            @Test
            @DisplayName("에러가 발생하지 않는다.")
            void it_does_not_throw() {
                assertThatCode(() -> {
                    jwtCredentialAuthenticator.authenticate(validJwtCredential);
                }).doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("빈 Jwt 자격 증명일 경우")
        class Context_emptyJwtCredentials {
            private List<String> emptyJwtCredentials;

            @BeforeEach
            void setUp() {
                emptyJwtCredentials = Arrays.asList(
                        null,
                        ""
                );
            }

            @Test
            @DisplayName("EmptyJwtCredentialsException 에러를 던진다")
            void it_throws_EmptyJwtCredentialsException() {
                for (String emptyJwtCredential : emptyJwtCredentials) {
                    assertThatThrownBy(() -> {
                        jwtCredentialAuthenticator.authenticate(emptyJwtCredential);
                    }).isInstanceOf(EmptyJwtCredentialException.class);
                }
            }
        }

        @Nested
        @DisplayName("올바르지 않은 Jwt 자격 증명이 주어질 경우")
        class Context_invalidJwtCredential {
            private List<String> invalidJwtCredentials;

            @BeforeEach
            void setUp() {
                invalidJwtCredentials = Arrays.asList(
                        VALID_JWT_CREDENTIAL + "x",
                        VALID_JWT_CREDENTIAL.substring(0, VALID_JWT_CREDENTIAL.length() - 1)
                );
            }

            @Test
            @DisplayName("SignatureException 에러를 던진다")
            void it_throws_SignatureException() {
                for (String invalidJwtCredential : invalidJwtCredentials) {
                    assertThatThrownBy(() -> {
                        jwtCredentialAuthenticator.authenticate(invalidJwtCredential);
                    }).isInstanceOf(SignatureException.class);
                }
            }
        }
    }

    @Nested
    @DisplayName("decode 메서드는")
    class Describe_decode {

        @Nested
        @DisplayName("Jwt 자격증명과 userId 키가 주어질 때")
        class Context_withJwtCredentialAndKey {
            private String jwtCredential;
            private String key;

            @BeforeEach
            void setUp() {
                this.jwtCredential = VALID_JWT_CREDENTIAL;
                this.key = "userId";
            }

            @Test
            @DisplayName("Long 타입의 복호화된 userId를 리턴한다")
            void It_returnsDecodedInfo() {
                Object userId = jwtCredentialAuthenticator.decode(jwtCredential, key);

                assertThat(userId).isInstanceOf(Long.class);
                assertThat(userId).isEqualTo(1L);
            }
        }

    }
}

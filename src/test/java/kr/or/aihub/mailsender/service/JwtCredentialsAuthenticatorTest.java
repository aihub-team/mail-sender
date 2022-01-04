package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.errors.EmptyJwtCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtCredentialsAuthenticatorTest {
    private static final String VALID_JWT_CREDENTIAL = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    @Autowired
    private JwtCredentialsAuthenticator jwtCredentialsAuthenticator;

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
                    jwtCredentialsAuthenticator.authenticate(validJwtCredential);
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
                        jwtCredentialsAuthenticator.authenticate(emptyJwtCredential);
                    }).isInstanceOf(EmptyJwtCredentialsException.class);
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
            void it_throw_SignatureException() {
                for (String invalidJwtCredential : invalidJwtCredentials) {
                    assertThatThrownBy(() -> {
                        jwtCredentialsAuthenticator.authenticate(invalidJwtCredential);
                    }).isInstanceOf(SignatureException.class);
                }
            }
        }
    }
}

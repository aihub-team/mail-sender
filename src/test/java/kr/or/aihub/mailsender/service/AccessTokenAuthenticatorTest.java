package kr.or.aihub.mailsender.service;

import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.errors.EmptyAccessTokenException;
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
class AccessTokenAuthenticatorTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    @Autowired
    private AccessTokenAuthenticator accessTokenAuthenticator;

    @Nested
    @DisplayName("authenticate 메서드는")
    class Describe_authenticate {

        @Nested
        @DisplayName("올바른 액세스 토큰이 주어질 경우")
        class Context_validToken {
            private String validAccessToken;

            @BeforeEach
            void setUp() {
                validAccessToken = VALID_TOKEN;
            }

            @Test
            @DisplayName("에러가 발생하지 않는다.")
            void it_does_not_throw() {
                assertThatCode(() -> {
                    accessTokenAuthenticator.authenticate(validAccessToken);
                }).doesNotThrowAnyException();
            }
        }

        @Nested
        @DisplayName("빈 토큰이 주어진 경우")
        class Context_emptyAccessToken {
            private List<String> emptyAccessTokens;

            @BeforeEach
            void setUp() {
                emptyAccessTokens = Arrays.asList(
                        null,
                        ""
                );
            }

            @Test
            @DisplayName("에러를 던진다")
            void it_throws_InvalidAccessTokenException() {
                for (String emptyAccessToken : emptyAccessTokens) {
                    assertThatThrownBy(() -> {
                        accessTokenAuthenticator.authenticate(emptyAccessToken);
                    }).isInstanceOf(EmptyAccessTokenException.class);
                }
            }
        }

        @Nested
        @DisplayName("올바르지 않은 액세스 토큰이 주어질 경우")
        class Context_invalidAccessToken {
            private List<String> invalidAccessTokens;

            @BeforeEach
            void setUp() {
                invalidAccessTokens = Arrays.asList(
                        VALID_TOKEN + "x",
                        VALID_TOKEN.substring(0, VALID_TOKEN.length() - 1)
                );
            }

            @Test
            @DisplayName("SignatureException 에러를 던진다")
            void it_throw_SignatureException() {
                for (String invalidAccessToken : invalidAccessTokens) {
                    assertThatThrownBy(() -> {
                        accessTokenAuthenticator.authenticate(invalidAccessToken);
                    }).isInstanceOf(SignatureException.class);
                }
            }
        }
    }
}

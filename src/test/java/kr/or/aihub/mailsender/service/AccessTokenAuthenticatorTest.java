package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
            @DisplayName("true를 리턴한다")
            void it_returns_true() {
                boolean actual = accessTokenAuthenticator.authenticate(validAccessToken);

                assertThat(actual).isTrue();
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
                        VALID_TOKEN.substring(0, VALID_TOKEN.length() - 1),
                        null,
                        ""
                );
            }

            @Test
            @DisplayName("false를 리턴한다")
            void it_returns_false() {
                for (String invalidAccessToken : invalidAccessTokens) {
                    boolean actual = accessTokenAuthenticator.authenticate(invalidAccessToken);

                    assertThat(actual).isFalse();
                }
            }
        }
    }
}

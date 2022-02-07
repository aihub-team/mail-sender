package kr.or.aihub.mailsender.domain.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRegisterRequestTest {

    @Nested
    @DisplayName("matchPassword 메서드는")
    class Describe_matchPassword {

        @Nested
        @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않을 경우")
        class Context_notMatchPasswordAndConfirmPassword {
            private String password;
            private String confirmPassword;

            @BeforeEach
            void setUp() {
                this.password = "password";
                this.confirmPassword = "xxxxx";
            }

            @Test
            @DisplayName("false를 리턴한다")
            void It_returnsFalse() {
                UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                        .password(password)
                        .confirmPassword(confirmPassword)
                        .build();

                boolean actual = userRegisterRequest.matchPassword();

                assertThat(actual).isFalse();
            }
        }

        @Nested
        @DisplayName("비밀번호와 비밀번호 확인이 일치할 경우")
        class Context_matchPasswordAndConfirmPassword {
            private String password;
            private String confirmPassword;

            @BeforeEach
            void setUp() {
                this.password = "password";
                this.confirmPassword = "password";
            }

            @Test
            @DisplayName("true를 리턴한다")
            void It_returnsTrue() {
                UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                        .password(password)
                        .confirmPassword(confirmPassword)
                        .build();

                boolean actual = userRegisterRequest.matchPassword();

                assertThat(actual).isTrue();
            }
        }
    }
}

package kr.or.aihub.mailsender.domain.user.domain;

import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Nested
    @DisplayName("createWithPasswordEncoder 메서드는")
    class Describe_createWithPasswordEncoder {

        private String username;
        private String password;

        @BeforeEach
        void setUp() {
            this.username = "username";
            this.password = "password";
        }

        @Test
        @DisplayName("비밀번호가 암호화된 유저를 리턴한다")
        void It_returnsPasswordEncodedUser() {
            User user = User.createWithPasswordEncoder(username, password, passwordEncoder);

            assertThat(user.getPassword()).isNotEqualTo(password);

            UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                    .username(username)
                    .password(password)
                    .build();
            user = User.createWithPasswordEncoder(userRegisterRequest, passwordEncoder);

            assertThat(user.getPassword()).isNotEqualTo(password);
        }
    }

    @Nested
    @DisplayName("matchPassword 메서드는")
    class Describe_matchPassword {

        @Nested
        @DisplayName("같은 비밀번호가 주어질 경우")
        class Context_givenMatchPassword {
            private String userPassword;
            private String matchPassword;

            @BeforeEach
            void setUp() {
                this.userPassword = "password";
                this.matchPassword = this.userPassword;
            }

            @Test
            @DisplayName("true를 리턴한다")
            void It_returnsTrue() {
                User user = TestUserFactory.create(userPassword, passwordEncoder);

                boolean actual = user.matchPassword(matchPassword, passwordEncoder);

                assertThat(actual).isTrue();
            }
        }

        @Nested
        @DisplayName("다른 비밀번호가 주어진 경우")
        class Context_notMatchPassword {
            private String userPassword;
            private String notMatchPassword;

            @BeforeEach
            void setUp() {
                this.userPassword = "password";
                this.notMatchPassword = "xxxxx";
            }

            @Test
            @DisplayName("false를 리턴한다")
            void It_returnsFalse() {
                User user = TestUserFactory.create(userPassword, passwordEncoder);

                boolean actual = user.matchPassword(notMatchPassword, passwordEncoder);

                assertThat(actual).isFalse();
            }
        }
    }
}

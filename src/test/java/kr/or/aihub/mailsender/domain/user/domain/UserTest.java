package kr.or.aihub.mailsender.domain.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {

    @Nested
    @DisplayName("createWithPasswordEncoder 메서드는")
    class Describe_createWithPasswordEncoder {

        @Nested
        @DisplayName("인자가 3개 주어지면")
        class Context_givenThreeArguments {
            private String username;
            private String password;
            private PasswordEncoder passwordEncoder;

            @BeforeEach
            void setUp() {
                this.username = "username";
                this.password = "password";
                this.passwordEncoder = new BCryptPasswordEncoder();
            }

            @Test
            @DisplayName("비밀번호가 암호화된 유저를 리턴한다")
            void It_returnsPasswordEncodedUser() {
                User user = User.createWithPasswordEncoder(username, password, passwordEncoder);

                assertThat(user.getPassword()).isNotEqualTo(password);
            }
        }
    }
}
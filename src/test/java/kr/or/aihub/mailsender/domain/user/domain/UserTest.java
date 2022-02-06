package kr.or.aihub.mailsender.domain.user.domain;

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
}
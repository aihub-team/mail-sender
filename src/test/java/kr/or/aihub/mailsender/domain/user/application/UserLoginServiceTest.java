package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.error.NotMatchPasswordException;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserLoginServiceTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NOT_MATCH_PASSWORD = PASSWORD + "x";
    private static final String JWT_CREDENTIAL_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .username(USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .build();

        userRepository.save(user);
    }

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {

        @Nested
        @DisplayName("존재하는 유저이름이고 패스워드가 일치할 경우")
        class Context_existUsernameAndMatchPassword {
            private UserLoginRequest existUsernameAndMatchPasswordLoginRequest;

            @BeforeEach
            void setUp() {
                existUsernameAndMatchPasswordLoginRequest = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("Jwt 자격증명을 리턴한다")
            void It_returnsJwtCredential() {
                String jwtCredential = userLoginService.login(existUsernameAndMatchPasswordLoginRequest);

                assertThat(jwtCredential).matches(JWT_CREDENTIAL_REGEX);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저이름이 주어질 경우")
        class Context_notExistUsernameLoginRequest {
            private UserLoginRequest notExistUsernameLoginRequest;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                notExistUsernameLoginRequest = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void It_throwsUserNotFoundException() {
                assertThatThrownBy(() ->
                        userLoginService.login(notExistUsernameLoginRequest)
                ).isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("패스워드가 일치하지 않을 경우")
        class Context_notMatchPassword {
            private UserLoginRequest notMatchPasswordLoginRequest;

            @BeforeEach
            void setUp() {
                notMatchPasswordLoginRequest = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(NOT_MATCH_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("NotMatchPasswordException을 던진다")
            void It_throwsNotMatchPasswordException() {
                assertThatThrownBy(() ->
                        userLoginService.login(notMatchPasswordLoginRequest)
                ).isInstanceOf(NotMatchPasswordException.class);
            }
        }

    }
}

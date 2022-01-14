package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.error.PasswordNotMatchException;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
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
    private static final String JWT_CREDENTIAL_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {

        @Nested
        @DisplayName("존재하는 유저이름이고")
        class Context_existUsername {

            @Nested
            @DisplayName("비밀번호가 일치할 경우")
            class Context_passwordMatch {
                private UserLoginRequest matchPasswordLoginRequest;

                @BeforeEach
                void setUp() {
                    String username = "username";
                    String password = "password";
                    User user = TestUserFactory.create(username, password, passwordEncoder);

                    userRepository.save(user);

                    matchPasswordLoginRequest = UserLoginRequest.builder()
                            .username(username)
                            .password(password)
                            .build();
                }

                @Test
                @DisplayName("Jwt 자격증명을 리턴한다")
                void It_returnsJwtCredential() {
                    String jwtCredential = userLoginService.login(matchPasswordLoginRequest);

                    assertThat(jwtCredential).matches(JWT_CREDENTIAL_REGEX);
                }

            }

            @Nested
            @DisplayName("비밀번호가 일치하지 않을 경우")
            class Context_passwordNotMatch {
                private UserLoginRequest passwordNotMatchLoginRequest;

                @BeforeEach
                void setUp() {
                    String username = "username";
                    String password = "password";
                    User user = TestUserFactory.create(username, password, passwordEncoder);

                    userRepository.save(user);

                    passwordNotMatchLoginRequest = UserLoginRequest.builder()
                            .username(username)
                            .password("xxxxx")
                            .build();
                }

                @Test
                @DisplayName("PasswordNotMatchException을 던진다")
                void It_throwsPasswordNotMatchException() {
                    assertThatThrownBy(() ->
                            userLoginService.login(passwordNotMatchLoginRequest)
                    ).isInstanceOf(PasswordNotMatchException.class);
                }
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저이름이 주어질 경우")
        class Context_notExistUsernameLoginRequest {
            private UserLoginRequest notExistUsernameLoginRequest;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                String username = "username";
                String password = "password";

                notExistUsernameLoginRequest = UserLoginRequest.builder()
                        .username(username)
                        .password(password)
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

    }
}

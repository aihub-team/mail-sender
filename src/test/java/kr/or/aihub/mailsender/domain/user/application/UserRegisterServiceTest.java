package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import kr.or.aihub.mailsender.domain.user.error.ConfirmPasswordNotMatchException;
import kr.or.aihub.mailsender.domain.user.error.ExistUsernameException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
public class UserRegisterServiceTest {
    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("registerUser 메서드는")
    class Describe_registerUser {

        @Nested
        @DisplayName("새로운 회원이름일 경우")
        class Context_newUsername {
            private UserRegisterRequest newUsernameRegisterRequest;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                String username = "username";
                String password = "password";

                newUsernameRegisterRequest = UserRegisterRequest.builder()
                        .username(username)
                        .password(password)
                        .confirmPassword(password)
                        .build();
            }

            @Test
            @DisplayName("생성된 유저를 리턴한다")
            void it_returns_created_user() {
                assertThatCode(() -> {
                    User createdUser = userRegisterService.registerUser(newUsernameRegisterRequest);

                    assertThat(createdUser.getUsername())
                            .isEqualTo(newUsernameRegisterRequest.getUsername());
                    assertThat(createdUser.getPassword())
                            .isNotEqualTo(newUsernameRegisterRequest.getPassword());
                }).doesNotThrowAnyException();
            }

        }

        @Nested
        @DisplayName("존재하는 유저이름일 경우")
        class Context_existUsername {
            private UserRegisterRequest existUsernameRegisterRequest;

            @BeforeEach
            void setUp() {
                String username = "username";
                String password = "password";
                User user = TestUserFactory.create(username, password, passwordEncoder);

                userRepository.save(user);

                existUsernameRegisterRequest = UserRegisterRequest.builder()
                        .username(username)
                        .password(password)
                        .confirmPassword(password)
                        .build();
            }

            @Test
            @DisplayName("ExistUsernameException을 던진다")
            void It_throwsExistUsernameException() {
                assertThatThrownBy(() -> {
                    userRegisterService.registerUser(existUsernameRegisterRequest);
                }).isInstanceOf(ExistUsernameException.class);
            }
        }

        @Nested
        @DisplayName("비밀번호 확인이 틀릴 경우")
        class Context_confirmPasswordNotMatch {
            private UserRegisterRequest confirmPasswordNotMatchRegisterRequest;

            @BeforeEach
            void setUp() {
                String username = "username";
                String password = "password";

                confirmPasswordNotMatchRegisterRequest = UserRegisterRequest.builder()
                        .username(username)
                        .password(password)
                        .confirmPassword("xxxxx")
                        .build();
            }

            @Test
            @DisplayName("ConfirmPasswordNotMatchException을 던진다")
            void It_throwsConfirmPasswordNotMatchException() {
                assertThatThrownBy(
                        () -> userRegisterService.registerUser(confirmPasswordNotMatchRegisterRequest)
                ).isInstanceOf(ConfirmPasswordNotMatchException.class);
            }
        }
    }
}

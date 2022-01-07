package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import kr.or.aihub.mailsender.domain.user.error.ExistUsernameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
public class UserRegisterServiceTest {
    private static final String USERNAME = "username";
    private static final String NEW_USERNAME = "newUsername";
    private static final String PASSWORD = "password";

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        userRepository.save(user);
    }

    @Nested
    @DisplayName("registerUser 메서드는")
    class Describe {

        @Nested
        @DisplayName("새로운 회원이름일 경우")
        class Context_newUsername {
            private UserRegisterRequest newUsernameRegisterRequest;

            @BeforeEach
            void setUp() {
                newUsernameRegisterRequest = UserRegisterRequest.builder()
                        .username(NEW_USERNAME)
                        .password(PASSWORD)
                        .verifyPassword(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("생성된 유저를 리턴한다")
            void it_returns_created_user() {
                assertThatCode(() -> {
                    User user = userRegisterService.registerUser(newUsernameRegisterRequest);

                    assertThat(user.getUsername()).isEqualTo(NEW_USERNAME);
                    assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
                }).doesNotThrowAnyException();
            }

        }

        @Nested
        @DisplayName("존재하는 유저이름일 경우")
        class Context_existUsername {
            private UserRegisterRequest existUsernameRegisterRequest;

            @BeforeEach
            void setUp() {
                String existUsername = USERNAME;

                existUsernameRegisterRequest = UserRegisterRequest.builder()
                        .username(existUsername)
                        .password(PASSWORD)
                        .verifyPassword(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("ExistUsernameException을 던진다")
            void It_throwsExistUsernameException() throws Exception {
                assertThatThrownBy(() -> {
                    userRegisterService.registerUser(existUsernameRegisterRequest);
                }).isInstanceOf(ExistUsernameException.class);
            }
        }

    }
}

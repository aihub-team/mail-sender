package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class UserRegisterServiceTest {
    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
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
                User createdUser = userRegisterService.registerUser(newUsernameRegisterRequest);

                assertThat(createdUser.getUsername())
                        .isEqualTo(newUsernameRegisterRequest.getUsername());
                assertThat(createdUser.getPassword())
                        .isNotEqualTo(newUsernameRegisterRequest.getPassword());

                // TODO: 2022/01/15 role도 검증 - deactivate 상태만 있어야 한다.
                List<Role> createdUserRoles = roleRepository.findAllByUser(createdUser);
                assertThat(createdUserRoles)
                        .hasSize(1);
                assertThat(createdUserRoles.get(0).getType())
                        .isEqualTo(RoleType.ROLE_DEACTIVATE);
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

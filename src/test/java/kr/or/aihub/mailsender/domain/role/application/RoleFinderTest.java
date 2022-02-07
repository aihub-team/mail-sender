package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_DEACTIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RoleFinderTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleFinder roleFinder;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("findBy 메서드는")
    class Describe_findBy {

        @Nested
        @DisplayName("존재하는 userId가 주어지면")
        class Context_existUserId {
            private Long existUserId;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                Role role = Role.create(user, ROLE_DEACTIVATE);
                roleRepository.save(role);

                this.existUserId = user.getId();
            }

            @Test
            @DisplayName("찾은 권한을 리턴한다")
            void It_returnsFoundRole() {
                List<Role> roles = roleFinder.findBy(existUserId);

                assertThat(roles).hasSize(1);
                assertThat(roles.get(0).getType())
                        .isEqualTo(ROLE_DEACTIVATE);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 userId가 주어지면")
        class Context_notExistUserId {
            private Long notExistUserId;

            @BeforeEach
            void setUp() {
                User notSavedUser = TestUserFactory.create(passwordEncoder);
                User savedUser = userRepository.save(notSavedUser);

                cleanUp();
                this.notExistUserId = savedUser.getId();
            }

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void It_throwsUserNotFoundException() {
                assertThatThrownBy(() -> roleFinder.findBy(notExistUserId))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }
}

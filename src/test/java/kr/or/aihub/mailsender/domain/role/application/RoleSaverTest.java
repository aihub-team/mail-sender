package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class RoleSaverTest {
    private RoleSaver roleSaver;

    private UserRepository userRepository = mock(UserRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);

    @BeforeEach
    void setUp() {
        this.roleSaver = new RoleSaver(userRepository, roleRepository);
    }

    @Nested
    @DisplayName("createAndSave 메서드는")
    class Describe_createAndSave {

        @Nested
        @DisplayName("존재하지 않는 userId가 주어지면")
        class Context_notExistUserId {
            private Long notExistUserId;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create();

                given(userRepository.findById(user.getId()))
                        .willReturn(Optional.empty());

                this.notExistUserId = user.getId();
            }

            @ParameterizedTest
            @EnumSource(RoleType.class)
            @DisplayName("UserNotFoundException을 던진다")
            void It_throwsUserNotFoundException(RoleType roleType) {
                assertThatThrownBy(() -> roleSaver.createAndSave(notExistUserId, roleType))
                        .isInstanceOf(UserNotFoundException.class);
            }

        }

        @Nested
        @DisplayName("존재하는 userId가 주어지면")
        class Context_existUserId {
            private Long existUserId;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create();

                given(userRepository.findById(user.getId()))
                        .willReturn(Optional.of(user));

                given(roleRepository.save(any(Role.class)))
                        .will(invocation -> {
                            Role role = invocation.getArgument(0);
                            RoleType roleType = role.getType();

                            return TestRoleFactory.create(roleType);
                        });

                this.existUserId = user.getId();
            }

            @ParameterizedTest
            @EnumSource(RoleType.class)
            @DisplayName("저장된 권한을 리턴한다")
            void It_returnsSavedRole(RoleType roleType) {
                Role role = roleSaver.createAndSave(existUserId, roleType);

                assertThat(role.getType()).isEqualTo(roleType);
            }

        }
    }
}

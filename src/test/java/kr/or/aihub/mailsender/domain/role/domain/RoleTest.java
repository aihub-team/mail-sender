package kr.or.aihub.mailsender.domain.role.domain;

import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_DEACTIVATE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class RoleTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Nested
        @DisplayName("User 인자 하나만 주어지면")
        class Context_givenOnlyUserArgument {
            private User user;

            @BeforeEach
            void setUp() {
                this.user = TestUserFactory.create(passwordEncoder);
            }

            @Test
            @DisplayName("DEACTIVATE 권한 타입으로 생성된다")
            void It_createDeactivateRoleType() {
                Role role = Role.create(user);

                assertThat(role.getType()).isEqualTo(ROLE_DEACTIVATE);
            }
        }

        @Nested
        @DisplayName("유저와 권한 타입 인자가 주어지면")
        class Context_givenUserAndRoleTypeArguments {
            private User user;

            @BeforeEach
            void setUp() {
                this.user = TestUserFactory.create(passwordEncoder);
            }

            @ParameterizedTest
            @EnumSource(RoleType.class)
            @DisplayName("주어진 권한 타입 인자를 가진 권한이 생성된다")
            void It_createRoleWithGivenRoleType(RoleType roleType) {
                Role role = Role.create(user, roleType);

                assertThat(role.getType()).isEqualTo(roleType);
            }
        }
    }
}
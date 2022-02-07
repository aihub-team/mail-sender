package kr.or.aihub.mailsender.domain.role.domain;

import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_ACTIVATE;
import static kr.or.aihub.mailsender.domain.role.domain.RoleType.ROLE_DEACTIVATE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleTest {

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {

        @Nested
        @DisplayName("User 인자 하나만 주어지면")
        class Context_givenOnlyUserArgument {
            private User user;

            @BeforeEach
            void setUp() {
                this.user = TestUserFactory.create();
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
                this.user = TestUserFactory.create();
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

    @Nested
    @DisplayName("isActivateType 메서드는")
    class Describe_isActivateType {

        @Nested
        @DisplayName("Activate 타입이 주어지면")
        class Context_activateType {
            private RoleType activateRoleType;

            @BeforeEach
            void setUp() {
                this.activateRoleType = ROLE_ACTIVATE;
            }

            @Test
            @DisplayName("true를 리턴한다")
            void It_returnsTrue() {
                Role role = TestRoleFactory.create(activateRoleType);

                boolean actual = role.isActivateType();

                assertThat(actual).isTrue();
            }
        }

        @Nested
        @DisplayName("Activate가 아닌 타입이 주어지면")
        class Context_notActivateType {

            @ParameterizedTest
            @EnumSource(
                    value = RoleType.class,
                    names = {"ROLE_ACTIVATE"},
                    mode = EnumSource.Mode.EXCLUDE
            )
            @DisplayName("false를 리턴한다")
            void It_returnsFalse(RoleType notActivateRoleType) {
                Role role = TestRoleFactory.create(notActivateRoleType);

                boolean actual = role.isActivateType();

                assertThat(actual).isFalse();
            }
        }
    }
}

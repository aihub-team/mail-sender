package kr.or.aihub.mailsender.domain.role.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTypeTest {

    @Nested
    @DisplayName("isAdmin 메서드는")
    class Describe_isAdmin {

        @Nested
        @DisplayName("어드민 권한 타입일 경우")
        class Context_adminRoleType {
            private RoleType adminRoleType;

            @BeforeEach
            void setUp() {
                this.adminRoleType = RoleType.ROLE_ADMIN;
            }

            @Test
            @DisplayName("true를 리턴한다")
            void It_returnsTrue() {
                RoleType roleType = adminRoleType;

                boolean actual = roleType.isAdmin();

                assertThat(actual).isTrue();
            }

        }

        @Nested
        @DisplayName("어드민이 아닌 권한 타입일 경우")
        class Context_notAdminRoleType {

            @ParameterizedTest
            @EnumSource(
                    value = RoleType.class,
                    names = {"ROLE_ADMIN"},
                    mode = EnumSource.Mode.EXCLUDE
            )
            @DisplayName("false를 리턴한다")
            void It_returnsFalse(RoleType notAdminRoleType) {
                boolean actual = notAdminRoleType.isAdmin();

                assertThat(actual).isFalse();
            }
        }
    }
}

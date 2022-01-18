package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.role.dto.RoleAddRequest;
import kr.or.aihub.mailsender.domain.role.errors.AlreadyGrantedRoleException;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.error.DeactivateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("RoleAddService 클래스")
@SpringBootTest
@AutoConfigureMockMvc
class RoleAddServiceTest {

    @Autowired
    private RoleAddService roleAddService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Nested
    @DisplayName("add 메서드는")
    class Describe_add {

        @Nested
        @DisplayName("비활성화된 userId이고")
        class Context_deactivateUserId {
            private Long deactivateUserId;

            @BeforeEach
            void setUp() {
                User deactivateUser = TestUserFactory.create(passwordEncoder);
                userRepository.save(deactivateUser);

                Role role = TestRoleFactory.create(deactivateUser, RoleType.ROLE_DEACTIVATE);
                roleRepository.save(role);

                this.deactivateUserId = deactivateUser.getId();
            }

            @Nested
            @DisplayName("비활성화 권한 추가 요청일 경우")
            class Context_deactivateRoleAddRequest {
                private RoleAddRequest deactivateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                }

                @Test
                @DisplayName("이미 부여된 권한 예외를 던진다")
                void It_throwsAlreadyGrantedRoleException() {
                    assertThatThrownBy(() -> roleAddService.add(deactivateUserId, this.deactivateRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }

            @Nested
            @DisplayName("활성화 권한 추가 요청일 경우")
            class Context_activateRoleAddRequest {
                private RoleAddRequest activateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                }

                @Test
                @DisplayName("부여된 권한을 리턴한다")
                void It_returnsGrantedRole() {
                    RoleType roleType = roleAddService.add(deactivateUserId, this.activateRoleAddRequest);

                    assertThat(roleType).isEqualTo(RoleType.ROLE_ACTIVATE);
                }
            }

            @Nested
            @DisplayName("어드민 권한 추가 요청일 경우")
            class Context_adminRoleAddRequest {
                private RoleAddRequest adminRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                }

                @Test
                @DisplayName("비활성화된 유저 예외를 던진다")
                void It_throwsDeactivateUser() {
                    assertThatThrownBy(() -> roleAddService.add(deactivateUserId, this.adminRoleAddRequest))
                            .isInstanceOf(DeactivateUserException.class);
                }
            }
        }

        @Nested
        @DisplayName("활성화된 userId이고")
        class Context_activateUserId {
            private Long activateUserId;

            @BeforeEach
            void setUp() {
                User activateUser = TestUserFactory.create(passwordEncoder);
                userRepository.save(activateUser);

                Role deactivateRole = TestRoleFactory.create(activateUser, RoleType.ROLE_DEACTIVATE);
                Role activateRole = TestRoleFactory.create(activateUser, RoleType.ROLE_ACTIVATE);
                roleRepository.save(deactivateRole);
                roleRepository.save(activateRole);

                this.activateUserId = activateUser.getId();
            }

            @Nested
            @DisplayName("비활성화 권한 추가 요청일 경우")
            class Context_deactivateRoleAddRequest {
                private RoleAddRequest deactivateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                }

                @Test
                @DisplayName("이미 부여된 권한 예외를 던진다")
                void It_throwsAlreadyGrantedRoleException() {
                    assertThatThrownBy(() -> roleAddService.add(activateUserId, this.deactivateRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }

            @Nested
            @DisplayName("활성화 권한 추가 요청일 경우")
            class Context_activateRoleAddRequest {
                private RoleAddRequest activateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                }

                @Test
                @DisplayName("부여된 권한을 리턴한다")
                void It_returnsGrantedRole() {
                    assertThatThrownBy(() -> roleAddService.add(activateUserId, this.activateRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }

            @Nested
            @DisplayName("어드민 권한 추가 요청일 경우")
            class Context_adminRoleAddRequest {
                private RoleAddRequest adminRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                }

                @Test
                @DisplayName("부여된 권한을 리턴한다")
                void It_throws() {
                    RoleType actual = roleAddService.add(activateUserId, this.adminRoleAddRequest);

                    assertThat(actual).isEqualTo(RoleType.ROLE_ADMIN);
                }
            }
        }

        @Nested
        @DisplayName("어드민 userId이고")
        class Context_adminUserId {
            private Long adminUserId;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                Role deactivateRole = TestRoleFactory.create(user, RoleType.ROLE_DEACTIVATE);
                Role activateRole = TestRoleFactory.create(user, RoleType.ROLE_ACTIVATE);
                Role adminRole = TestRoleFactory.create(user, RoleType.ROLE_ADMIN);
                roleRepository.save(deactivateRole);
                roleRepository.save(activateRole);
                roleRepository.save(adminRole);

                this.adminUserId = user.getId();
            }

            @Nested
            @DisplayName("비활성화 권한 추가 요청일 경우")
            class Context_deactivateRoleAddRequest {
                private RoleAddRequest deactivateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                }

                @Test
                @DisplayName("이미 부여된 권한 예외를 던진다")
                void It_throwsAlreadyGrantedRoleException() {
                    assertThatThrownBy(() -> roleAddService.add(adminUserId, this.deactivateRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }

            @Nested
            @DisplayName("활성화 권한 추가 요청일 경우")
            class Context_activateRoleAddRequest {
                private RoleAddRequest activateRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                }

                @Test
                @DisplayName("이미 부여된 권한 예외를 던진다")
                void It_throwsAlreadyGrantedRoleException() {
                    assertThatThrownBy(() -> roleAddService.add(adminUserId, this.activateRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }

            @Nested
            @DisplayName("어드민 권한 추가 요청일 경우")
            class Context_adminRoleAddRequest {
                private RoleAddRequest adminRoleAddRequest;

                @BeforeEach
                void setUp() {
                    this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                }

                @Test
                @DisplayName("이미 부여된 권한 예외를 던진다")
                void It_throwsAlreadyGrantedRoleException() {
                    assertThatThrownBy(() -> roleAddService.add(adminUserId, this.adminRoleAddRequest))
                            .isInstanceOf(AlreadyGrantedRoleException.class);
                }
            }
        }
    }
}
package kr.or.aihub.mailsender.domain.role.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.role.dto.RoleAddRequest;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RoleController 클래스")
@SpringBootTest
@AutoConfigureMockMvc
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtCredentialEncoder jwtCredentialEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /role/add 요청은")
    class Describe_postRoleAdd {
        private final MockHttpServletRequestBuilder requestBuilder
                = post("/role/add");

        @Nested
        @DisplayName("어드민 권한을 가진 유저의 요청이고")
        class Context_withAdminRoleUserRequest {
            private String adminUserJwtCredential;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                Role role = TestRoleFactory.create(user, RoleType.ROLE_ADMIN);
                roleRepository.save(role);

                adminUserJwtCredential = jwtCredentialEncoder.encode(user.getId());
            }

            @Nested
            @DisplayName("비활성화된 유저이고")
            class Context_deactivateUser {
                private User deactivateUser;

                @BeforeEach
                void setUp() {
                    User deactivateUser = TestUserFactory.create(passwordEncoder);
                    userRepository.save(deactivateUser);

                    Role role = TestRoleFactory.create(deactivateUser, RoleType.ROLE_DEACTIVATE);
                    roleRepository.save(role);

                    this.deactivateUser = deactivateUser;
                }


                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("201을 응답한다")
                    void It_response201() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isCreated());
                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());

                    }
                }

            }

            @Nested
            @DisplayName("활성화된 유저이고")
            class Context_activateUser {
                private User activateUser;

                @BeforeEach
                void setUp() {
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    Role role = TestRoleFactory.create(user, RoleType.ROLE_ACTIVATE);
                    roleRepository.save(role);

                    this.activateUser = user;
                }

                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("201을 응답한다")
                    void It_response201() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isCreated());

                    }
                }

            }

            @Nested
            @DisplayName("어드민 유저이고")
            class Context_adminUser {
                private User adminUser;

                @BeforeEach
                void setUp() {
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    Role role = TestRoleFactory.create(user, RoleType.ROLE_ADMIN);
                    roleRepository.save(role);

                    this.adminUser = user;
                }

                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + adminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isBadRequest());

                    }
                }
            }

        } // 어드민 유저

        @Nested
        @DisplayName("어드민이 아닌 유저의 Jwt 자격증명이 주어지고")
        class Context_withNotAdminUserJwtCredential {
            private String notAdminUserJwtCredential;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                Role deactivateRole = TestRoleFactory.create(user, RoleType.ROLE_DEACTIVATE);
                Role activateRole = TestRoleFactory.create(user, RoleType.ROLE_ACTIVATE);
                roleRepository.save(deactivateRole);
                roleRepository.save(activateRole);

                this.notAdminUserJwtCredential = jwtCredentialEncoder.encode(user.getId());
            }

            @Nested
            @DisplayName("비활성화된 유저이고")
            class Context_deactivateUser {
                private User deactivateUser;

                @BeforeEach
                void setUp() {
                    User deactivateUser = TestUserFactory.create(passwordEncoder);
                    userRepository.save(deactivateUser);

                    Role role = TestRoleFactory.create(deactivateUser, RoleType.ROLE_DEACTIVATE);
                    roleRepository.save(role);

                    this.deactivateUser = deactivateUser;
                }

                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());
                    }
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());

                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", deactivateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());


                    }
                }

            }

            @Nested
            @DisplayName("활성화된 유저이고")
            class Context_activateUser {
                private User activateUser;

                @BeforeEach
                void setUp() {
                    User activateUser = TestUserFactory.create(passwordEncoder);
                    userRepository.save(activateUser);

                    Role role = TestRoleFactory.create(activateUser, RoleType.ROLE_ACTIVATE);
                    roleRepository.save(role);

                    this.activateUser = activateUser;
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());

                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());


                    }
                }


                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", activateUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());


                    }
                }

            }

            @Nested
            @DisplayName("어드민 유저이고")
            class Context_adminUser {
                private User adminUser;

                @BeforeEach
                void setUp() {
                    User adminUser = TestUserFactory.create(passwordEncoder);
                    userRepository.save(adminUser);

                    Role role = TestRoleFactory.create(adminUser, RoleType.ROLE_ADMIN);
                    roleRepository.save(role);

                    this.adminUser = adminUser;
                }

                @Nested
                @DisplayName("비활성화 권한 추가 요청이면")
                class Context_deactivateRoleAddRequest {
                    private RoleAddRequest deactivateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.deactivateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_DEACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());

                    }
                }

                @Nested
                @DisplayName("활성화 권한 추가 요청이면")
                class Context_activateRoleAddRequest {
                    private RoleAddRequest activateRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.activateRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ACTIVATE);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.activateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());

                    }

                }

                @Nested
                @DisplayName("어드민 권한 추가 요청이면")
                class Context_adminRoleAddRequest {
                    private RoleAddRequest adminRoleAddRequest;

                    @BeforeEach
                    void setUp() {
                        this.adminRoleAddRequest = new RoleAddRequest(RoleType.ROLE_ADMIN);
                    }

                    @Test
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.adminRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + notAdminUserJwtCredential)
                        );

                        action
                                .andExpect(status().isForbidden());
                    }
                }
            }
        }
    }
}
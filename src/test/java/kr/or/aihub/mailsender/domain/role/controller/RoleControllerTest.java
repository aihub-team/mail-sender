package kr.or.aihub.mailsender.domain.role.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.role.dto.RoleAddRequest;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomActivateUser;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomAdminUser;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("RoleController 클래스")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void saveDeactivateRoles(User user) {
        List<RoleType> roleTypes = Arrays.asList(
                RoleType.ROLE_DEACTIVATE
        );

        saveRoles(user, roleTypes);
    }

    private void saveActivateRoles(User user) {
        List<RoleType> roleTypes = Arrays.asList(
                RoleType.ROLE_DEACTIVATE,
                RoleType.ROLE_ACTIVATE
        );

        saveRoles(user, roleTypes);
    }

    private void saveAdminRoles(User user) {
        List<RoleType> roleTypes = Arrays.asList(
                RoleType.ROLE_DEACTIVATE,
                RoleType.ROLE_ACTIVATE,
                RoleType.ROLE_ADMIN
        );

        saveRoles(user, roleTypes);
    }

    private void saveRoles(User user, List<RoleType> roleTypes) {
        roleTypes.stream()
                .map(roleType -> Role.create(user, roleType))
                .forEach(role -> roleRepository.save(role));
    }

    @Nested
    @DisplayName("POST /role/add 요청은")
    class Describe_postRoleAdd {
        private final MockHttpServletRequestBuilder requestBuilder
                = post("/role/add");

        @Nested
        @DisplayName("어드민 권한을 가진 유저의 요청이고")
        @WithMockCustomAdminUser
        class Context_withAdminRoleUserRequest {

            @Nested
            @DisplayName("비활성화된 유저이고")
            class Context_deactivateUser {
                private User deactivateUser;

                @BeforeEach
                void setUp() {
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    saveDeactivateRoles(user);

                    this.deactivateUser = user;
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

                    saveActivateRoles(user);

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

                    saveAdminRoles(user);

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
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }
            }

        } // 어드민 유저

        @Nested
        @DisplayName("어드민이 아닌 유저의 요청이고")
        @WithMockCustomActivateUser
        class Context_withNotAdminUserJwtCredential {

            @Nested
            @DisplayName("비활성화된 유저이고")
            class Context_deactivateUser {
                private User deactivateUser;

                @BeforeEach
                void setUp() {
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    saveDeactivateRoles(user);

                    this.deactivateUser = user;
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
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    saveActivateRoles(user);

                    this.activateUser = user;
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
                    User user = TestUserFactory.create(passwordEncoder);
                    userRepository.save(user);

                    saveAdminRoles(user);

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
                    @DisplayName("403을 응답한다")
                    void It_response403() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .param("userId", adminUser.getId().toString())
                                        .content(new ObjectMapper().writeValueAsString(this.deactivateRoleAddRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
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
                        );

                        action
                                .andExpect(status().isForbidden());
                    }
                }
            }
        }
    }
}
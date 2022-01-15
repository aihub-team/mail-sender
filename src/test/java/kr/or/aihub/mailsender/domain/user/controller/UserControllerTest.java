package kr.or.aihub.mailsender.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
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

import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final String JWT_CREDENTIAL_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /user/login 요청은")
    class Describe_postUserLogin {
        private final MockHttpServletRequestBuilder requestBuilder = post("/user/login");

        @Nested
        @DisplayName("올바르지 않은 데이터가 주어질 경우")
        class Context_invalidData {
            private List<UserLoginRequest> invalidUserLoginRequestList;

            @BeforeEach
            void setUp() {
                String username = "username";
                String password = "password";

                invalidUserLoginRequestList = Arrays.asList(
                        new UserLoginRequest(null, null),
                        new UserLoginRequest(username, null),
                        new UserLoginRequest(null, password),
                        new UserLoginRequest("1", password),
                        new UserLoginRequest("123456789012345678901", password),
                        new UserLoginRequest(username, "123"),
                        new UserLoginRequest(username, "123456789012345678901")
                );
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                for (UserLoginRequest invalidUserLoginRequest : invalidUserLoginRequestList) {
                    ResultActions actions = mockMvc.perform(
                            post("/user/login")
                                    .content(objectMapper.writeValueAsString(invalidUserLoginRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                    );

                    actions
                            .andExpect(status().isBadRequest());
                }
            }
        }

        @Nested
        @DisplayName("올바른 데이터가 주어질 경우")
        class Context_validUserLoginRequest {

            @Nested
            @DisplayName("존재하지 않는 유저일 경우")
            class Context_notExistUser {
                private UserLoginRequest notExistUserLoginRequest;

                @BeforeEach
                void setUp() {
                    userRepository.deleteAll();

                    notExistUserLoginRequest = UserLoginRequest.builder()
                            .username("username")
                            .password("password")
                            .build();
                }

                @Test
                @DisplayName("400을 응답한다")
                void it_response_400() throws Exception {
                    ResultActions action = mockMvc.perform(
                            requestBuilder
                                    .content(objectMapper.writeValueAsString(notExistUserLoginRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                    );

                    action
                            .andExpect(status().isBadRequest());
                }
            }

            @Nested
            @DisplayName("존재하는 유저이고")
            class Context_existUser {

                @Nested
                @DisplayName("비밀번호가 일치하지 않을 경우")
                class Context_passwordNotMatch {
                    private UserLoginRequest passwordNotMatchLoginRequest;

                    @BeforeEach
                    void setUp() {
                        String username = "username";
                        String password = "password";
                        User user = TestUserFactory.create(username, password, passwordEncoder);

                        userRepository.save(user);

                        passwordNotMatchLoginRequest = UserLoginRequest.builder()
                                .username(username)
                                .password("xxxxx")
                                .build();
                    }

                    @Test
                    @DisplayName("400을 응답한다")
                    void It_response400() throws Exception {
                        ResultActions action = mockMvc.perform(
                                requestBuilder
                                        .content(objectMapper.writeValueAsString(passwordNotMatchLoginRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                        );

                        action
                                .andExpect(status().isBadRequest());
                    }
                }

                @Nested
                @DisplayName("비밀번호가 일치하고")
                class Context_passwordMatch {

                    @Nested
                    @DisplayName("비활성화된 유저일 경우")
                    class Context_deactivate {
                        private UserLoginRequest deactivateLoginRequest;

                        @BeforeEach
                        void setUp() {
                            String username = "username";
                            String password = "password";
                            User user = TestUserFactory.create(username, password, passwordEncoder);
                            userRepository.save(user);

                            Role role = TestRoleFactory.create(user, RoleType.ROLE_DEACTIVATE);
                            roleRepository.save(role);

                            deactivateLoginRequest = UserLoginRequest.builder()
                                    .username(username)
                                    .password(password)
                                    .build();
                        }

                        @Test
                        @DisplayName("401을 응답한다")
                        void it_response_401() throws Exception {
                            ResultActions perform = mockMvc.perform(
                                    requestBuilder
                                            .content(objectMapper.writeValueAsString(deactivateLoginRequest))
                                            .contentType(MediaType.APPLICATION_JSON)
                            );

                            perform
                                    .andExpect(status().isUnauthorized());
                        }
                    }

                    @Nested
                    @DisplayName("활성화된 유저일 경우")
                    class Context_activate {
                        private UserLoginRequest activateUserLoginRequest;

                        @BeforeEach
                        void setUp() {
                            String username = "username";
                            String password = "password";
                            User user = TestUserFactory.create(username, password, passwordEncoder);
                            userRepository.save(user);

                            Role role = TestRoleFactory.create(user, RoleType.ROLE_ACTIVATE);
                            roleRepository.save(role);

                            activateUserLoginRequest = UserLoginRequest.builder()
                                    .username(username)
                                    .password(password)
                                    .build();
                        }

                        @Test
                        @DisplayName("201과 액세스 토큰을 응답한다")
                        void it_response_201_and_return_accessToken() throws Exception {
                            ResultActions action = mockMvc.perform(
                                    requestBuilder
                                            .content(objectMapper.writeValueAsString(activateUserLoginRequest))
                                            .contentType(MediaType.APPLICATION_JSON)
                            );

                            action
                                    .andExpect(status().isCreated())
                                    .andExpect(jsonPath("$.accessToken").value(matchesRegex(JWT_CREDENTIAL_REGEX)));
                        }

                    }
                }

            }

        }

    }

    @Nested
    @DisplayName("POST /user/register 요청은")
    class Describe_postUserRegister {
        private final MockHttpServletRequestBuilder requestBuilder = post("/user/register");

        @Nested
        @DisplayName("새 유저이름일 경우")
        class Context_newUsername {

            private UserRegisterRequest newUsernameRegisterRequest;

            @BeforeEach
            void setUp() {
                String username = "username";
                String password = "password";

                newUsernameRegisterRequest = UserRegisterRequest.builder()
                        .username(username)
                        .password(password)
                        .confirmPassword(password)
                        .build();
            }

            @Test
            @DisplayName("302를 응답하고 로그인 페이지로 리다이렉트 된다")
            void It_response302AndRedirectLoginPage() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(newUsernameRegisterRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/user/login"));
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
            @DisplayName("400을 응답한다")
            void It_response400() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(existUsernameRegisterRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isBadRequest());
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
            @DisplayName("400을 응답한다")
            void It_response400() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(confirmPasswordNotMatchRegisterRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isBadRequest());
            }
        }
    }
}

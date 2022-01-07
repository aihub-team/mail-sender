package kr.or.aihub.mailsender.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
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
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final  String NOT_MATCH_PASSWORD = PASSWORD + "x";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /user/login 요청은")
    class Describe_postUserLogin {
        private final MockHttpServletRequestBuilder requestBuilder = post("/user/login");

        @Nested
        @DisplayName("올바른 데이터가 주어질 경우")
        class Context_validUserLoginRequest {
            private UserLoginRequest validUserLoginRequest;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                User user = User.builder()
                        .username(USERNAME)
                        .password(passwordEncoder.encode(PASSWORD))
                        .build();
                userRepository.save(user);

                validUserLoginRequest = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("201과 액세스 토큰을 응답한다")
            void it_response_201_and_return_accessToken() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(validUserLoginRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken").value(matchesRegex(JWT_CREDENTIAL_REGEX)));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저일 경우")
        class Context_notExistUser {
            private UserLoginRequest notExistUserLoginRequestData;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                notExistUserLoginRequestData = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(notExistUserLoginRequestData))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("비밀번호가 일치하지 않을 경우")
        class Context_notMatchPassword {
            private UserLoginRequest notMatchPasswordUserLoginRequest;

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                User user = User.builder()
                        .username(USERNAME)
                        .password(passwordEncoder.encode(PASSWORD))
                        .build();
                userRepository.save(user);

                notMatchPasswordUserLoginRequest = UserLoginRequest.builder()
                        .username(USERNAME)
                        .password(NOT_MATCH_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("400을 응답한다")
            void It_response400() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(notMatchPasswordUserLoginRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 데이터가 주어질 경우")
        class Context_invalidData {
            private List<UserLoginRequest> invalidUserLoginRequestList;

            @BeforeEach
            void setUp() {
                invalidUserLoginRequestList = Arrays.asList(
                        new UserLoginRequest(null, null),
                        new UserLoginRequest(USERNAME, null),
                        new UserLoginRequest(null, PASSWORD),
                        new UserLoginRequest("1", PASSWORD),
                        new UserLoginRequest("123456789012345678901", PASSWORD),
                        new UserLoginRequest(USERNAME, "123"),
                        new UserLoginRequest(USERNAME, "123456789012345678901")
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
    }

    @Nested
    @DisplayName("POST /user/register 요청은")
    class Describe_postUserRegister {
        private final MockHttpServletRequestBuilder requestBuilder = post("/user/register");

        @Nested
        @DisplayName("올바른 데이터가 주어진 요청일 경우")
        class Context_validUserRegisterDataRequest {

            private UserRegisterRequest validUserRegisterRequest;

            @BeforeEach
            void setUp() {
                validUserRegisterRequest = UserRegisterRequest.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .verifyPassword(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("302를 응답하고 로그인 페이지로 리다이렉트 된다")
            void It_response302AndRedirectLoginPage() throws Exception {
                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .content(objectMapper.writeValueAsString(validUserRegisterRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                action
                        .andExpect(status().isFound())
                        .andExpect(redirectedUrl("/user/login"));
            }
        }
    }
}

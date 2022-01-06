package kr.or.aihub.mailsender.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /user/login 요청은")
    class Describe_postUserLogin {
        private UserLoginRequest userLoginRequest;

        @Nested
        @DisplayName("올바른 데이터가 주어질 경우")
        class Context_validData {

            @BeforeEach
            void setUp() {
                userLoginRequest = UserLoginRequest.builder()
                        .username("username")
                        .password("password")
                        .build();
            }

            @Test
            @DisplayName("201과 액세스 토큰을 응답한다")
            void it_response_201_and_return_accessToken() throws Exception {
                ResultActions actions =
                        mockMvc.perform(
                                post("/user/login")
                                        .content(objectMapper.writeValueAsString(userLoginRequest))
                                        .contentType(MediaType.APPLICATION_JSON)
                        );

                actions
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken").value(matchesRegex(JWT_CREDENTIAL_REGEX)));
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
                        new UserLoginRequest("username", null),
                        new UserLoginRequest(null, "password"),
                        new UserLoginRequest("1", "password"),
                        new UserLoginRequest("123456789012345678901", "password"),
                        new UserLoginRequest("username", "123"),
                        new UserLoginRequest("username", "123456789012345678901")
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
                        .username("username")
                        .password("password")
                        .verifyPassword("password")
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

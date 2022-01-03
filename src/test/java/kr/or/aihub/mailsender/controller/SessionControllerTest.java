package kr.or.aihub.mailsender.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.dto.SessionCreateRequestData;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerTest {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /session 요청은")
    class Describe_postSession {
        SessionCreateRequestData sessionCreateRequestData;

        @Nested
        @DisplayName("올바른 데이터가 주어질 경우")
        class Context_validData {

            @BeforeEach
            void setUp() {
                sessionCreateRequestData = SessionCreateRequestData.builder()
                        .username("username")
                        .password("password")
                        .build();
            }

            @Test
            @DisplayName("201과 액세스 토큰을 응답한다")
            void it_response_201_and_return_accessToken() throws Exception {
                ResultActions actions =
                        mockMvc.perform(
                                post("/session")
                                        .content(objectMapper.writeValueAsString(sessionCreateRequestData))
                                        .contentType(MediaType.APPLICATION_JSON)
                        );

                actions
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken").value(matchesRegex(TOKEN_REGEX)));
            }
        }

        @Nested
        @DisplayName("올바르지 않은 데이터가 주어질 경우")
        class Context_invalidData {
            private List<SessionCreateRequestData> invalidSessionCreateRequestDataList;

            @BeforeEach
            void setUp() {
                invalidSessionCreateRequestDataList = Arrays.asList(
                        new SessionCreateRequestData(null, null),
                        new SessionCreateRequestData("username", null),
                        new SessionCreateRequestData(null, "password"),
                        new SessionCreateRequestData("1", "password"),
                        new SessionCreateRequestData("123456789012345678901", "password"),
                        new SessionCreateRequestData("username", "123"),
                        new SessionCreateRequestData("username", "123456789012345678901")
                );
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                for (SessionCreateRequestData invalidSessionCreateRequestData : invalidSessionCreateRequestDataList) {
                    ResultActions actions = mockMvc.perform(
                            post("/session")
                                    .content(objectMapper.writeValueAsString(invalidSessionCreateRequestData))
                                    .contentType(MediaType.APPLICATION_JSON)
                    );

                    actions
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }
}

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
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

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
                        .andExpect(content().string(containsString(VALID_TOKEN)));
            }
        }

        @Nested
        @DisplayName("올바르지 않은 데이터가 주어질 경우")
        class Context_invalidData {
            private List<SessionCreateRequestData> invalidSessionCreateRequestDataList;

            @BeforeEach
            void setUp() {
                List<List<String>> inValidDataAttributes = Arrays.asList(
                        Arrays.asList(null, null),
                        Arrays.asList("username", null),
                        Arrays.asList(null, "password"),
                        Arrays.asList("1", "password"),
                        Arrays.asList("123456789012345678901", "password"),
                        Arrays.asList("username", "123"),
                        Arrays.asList("username", "123456789012345678901")
                );

                invalidSessionCreateRequestDataList = inValidDataAttributes.stream()
                        .map(getSessionCreateRequestData())
                        .collect(Collectors.toList());
            }

            private Function<List<String>, SessionCreateRequestData> getSessionCreateRequestData() {
                return it -> {
                    String username = it.get(0);
                    String password = it.get(1);

                    return SessionCreateRequestData.builder()
                            .username(username)
                            .password(password)
                            .build();
                };
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

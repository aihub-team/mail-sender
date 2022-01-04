package kr.or.aihub.mailsender.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET / 요청은")
    class Describe_getIndexRequest {

        @Nested
        @DisplayName("올바른 액세스 토큰이 주어지면")
        class Context_validAccessToken {

            @Test
            @DisplayName("201을 응답한다")
            void it_returns_201() throws Exception {
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/")
                                .header("Authorization", "Bearer " + VALID_TOKEN)
                );

                actions
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 액세스 토큰이 주어지면")
        class Context_invalidAccessToken {
            private List<String> invalidAccessTokens;

            @BeforeEach
            void setUp() {
                invalidAccessTokens = Arrays.asList(
                        VALID_TOKEN + "x",
                        VALID_TOKEN.substring(0, VALID_TOKEN.length() - 1)
                );
            }

            @Test
            @DisplayName("401을 응답한다")
            void it_response_401() throws Exception {
                for (String invalidAccessToken : invalidAccessTokens) {
                    ResultActions actions = mockMvc.perform(
                            MockMvcRequestBuilders.get("/")
                                    .header("Authorization", "Bearer " + invalidAccessToken)
                    );

                    actions
                            .andExpect(status().isUnauthorized());
                }
            }
        }

        @Nested
        @DisplayName("토큰이 주어지지 않는다면")
        class Context_emptyAccessToken {
            private List<String> emptyAccessTokens;

            @BeforeEach
            void setUp() {
                emptyAccessTokens = Arrays.asList(
                        "",
                        " "
                );
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                for (String emptyAccessToken : emptyAccessTokens) {
                    ResultActions actions = mockMvc.perform(
                            MockMvcRequestBuilders.get("/")
                                    .header("Authorization", "Bearer " + emptyAccessToken)
                    );

                    actions
                            .andExpect(status().isBadRequest());
                }
            }
        }
    }
}

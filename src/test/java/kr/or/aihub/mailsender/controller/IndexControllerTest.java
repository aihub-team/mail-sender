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
    private static final String VALID_JWT_CREDENTIAL = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET / 요청은")
    class Describe_getIndexRequest {

        @Nested
        @DisplayName("올바른 Jwt 자격 증명이 주어지면")
        class Context_validJwtCredential {

            @Test
            @DisplayName("201을 응답한다")
            void it_response_201() throws Exception {
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/")
                                .header("Authorization", "Bearer " + VALID_JWT_CREDENTIAL)
                );

                actions
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("올바르지 않은 Jwt 자격 증명이 주어지면")
        class Context_inValidJwtCredential {
            private List<String> inValidJwtCredentials;

            @BeforeEach
            void setUp() {
                inValidJwtCredentials = Arrays.asList(
                        VALID_JWT_CREDENTIAL + "x",
                        VALID_JWT_CREDENTIAL.substring(0, VALID_JWT_CREDENTIAL.length() - 1)
                );
            }

            @Test
            @DisplayName("401을 응답한다")
            void it_response_401() throws Exception {
                for (String invalidAccessToken : inValidJwtCredentials) {
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
        @DisplayName("Jwt 자격 증명이 주어지지 않는다면")
        class Context_emptyJwtCredential {
            private List<String> emptyJwtCredentials;

            @BeforeEach
            void setUp() {
                emptyJwtCredentials = Arrays.asList(
                        "",
                        " "
                );
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                for (String emptyJwtCredential : emptyJwtCredentials) {
                    ResultActions actions = mockMvc.perform(
                            MockMvcRequestBuilders.get("/")
                                    .header("Authorization", "Bearer " + emptyJwtCredential)
                    );

                    actions
                            .andExpect(status().isBadRequest());
                }
            }
        }

        @Nested
        @DisplayName("Authorization 헤더가 허용되지 않은 Jwt 타입이라면")
        class Context_notAllowedJwtType {
            private String notAllowedJwtType;

            @BeforeEach
            void setUp() {
                notAllowedJwtType = "ABCDEF ";
            }

            @Test
            @DisplayName("400을 응답한다")
            void it_response_400() throws Exception {
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get("/")
                                .header("Authorization", notAllowedJwtType + VALID_JWT_CREDENTIAL)
                );

                actions
                        .andExpect(status().isBadRequest());
            }
        }
    }
}

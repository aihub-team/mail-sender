package kr.or.aihub.mailsender.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.aihub.mailsender.dto.PostSessionRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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

    @Test
    @DisplayName("POST /session 요청은 올바른 PostSessionRequestData가 주어질 경우 201과 액세스 토큰을 응답한다")
    void postSession() throws Exception {
        PostSessionRequestData postSessionRequestData = PostSessionRequestData.builder()
                .username("username")
                .password("password")
                .build();

        ResultActions actions =
                mockMvc.perform(
                        post("/session")
                                .content(objectMapper.writeValueAsString(postSessionRequestData))
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(VALID_TOKEN)));
    }
}

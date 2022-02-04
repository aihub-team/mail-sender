package kr.or.aihub.mailsender;

import kr.or.aihub.mailsender.global.config.security.WithMockCustomActivateUser;
import kr.or.aihub.mailsender.global.config.security.WithMockCustomDeactivateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET / 요청은")
    class Describe_getIndexRequest {

        @Nested
        @DisplayName("활성화된 유저일 경우")
        @WithMockCustomActivateUser
        class Context_activateUser {

            @Test
            @DisplayName("200을 응답한다")
            void it_response_200() throws Exception {
                ResultActions actions = mockMvc.perform(
                        get("/")
                );

                actions
                        .andExpect(status().isOk());
            }

        }

        @Nested
        @DisplayName("활성화되지 않은 유저일 경우")
        @WithMockCustomDeactivateUser
        class Context_deactivateUser {

            @Test
            @DisplayName("403을 응답한다")
            void It_response403() throws Exception {
                ResultActions actions = mockMvc.perform(
                        get("/")
                );

                actions
                        .andExpect(status().isForbidden());
            }
        }
    }

}

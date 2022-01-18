package kr.or.aihub.mailsender.domain.mail.transactional.controller;

import kr.or.aihub.mailsender.domain.role.TestRoleFactory;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TransactionalMailController 클래스")
class TransactionalMailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtCredentialEncoder jwtCredentialEncoder;

    private void saveDeactivateRole(User user) {
        Role deactivateRole = TestRoleFactory.create(user, RoleType.ROLE_DEACTIVATE);

        roleRepository.save(deactivateRole);
    }

    private void saveActivateRole(User user) {
        Role activateRole = TestRoleFactory.create(user, RoleType.ROLE_ACTIVATE);

        roleRepository.save(activateRole);
    }

    @Nested
    @DisplayName("GET /mail/transactional/templates/send 요청은")
    class Describe_mailTransactionalTemplatesSend {
        private final MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/mail/transactional/templates/send");

        @Nested
        @DisplayName("인증된 유저일 경우")
        class Context_activateUser {
            private User activateUser;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                saveDeactivateRole(user);
                saveActivateRole(user);

                this.activateUser = user;
            }

            @Test
            @DisplayName("200을 응답한다")
            void It_response200() throws Exception {
                String jwtCredential = jwtCredentialEncoder.encode(activateUser.getId());

                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .header(
                                        "Authorization", "Bearer " + jwtCredential)
                );

                action
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("인증되지 않은 유저일 경우")
        class Context_deactivateUser {
            private User deactivateUser;

            @BeforeEach
            void setUp() {
                User user = TestUserFactory.create(passwordEncoder);
                userRepository.save(user);

                saveDeactivateRole(user);

                this.deactivateUser = user;
            }

            @Test
            @DisplayName("403을 응답한다")
            void It_response403() throws Exception {
                String jwtCredential = jwtCredentialEncoder.encode(deactivateUser.getId());

                ResultActions action = mockMvc.perform(
                        requestBuilder
                                .header("Authorization", "Bearer " + jwtCredential)
                );

                action
                        .andExpect(status().isForbidden());
            }
        }

    }
}

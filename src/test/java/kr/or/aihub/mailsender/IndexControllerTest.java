package kr.or.aihub.mailsender;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialEncoder;
import org.junit.jupiter.api.AfterEach;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {
    private static final String VALID_JWT_CREDENTIAL = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxIn0.0_CoL6BQVE07Y5M5kcGdmy8Mp6FTcNZdNfgo6hEsxdU";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtCredentialEncoder jwtCredentialEncoder;

    @AfterEach
    void cleanUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET / 요청은")
    class Describe_getIndexRequest {

        @Nested
        @DisplayName("올바른 Jwt 자격 증명이 주어지면")
        class Context_validJwtCredential {

            @Nested
            @DisplayName("활성화된 유저일 경우")
            class Context_activateUser {
                private String activateUserJwtCredential;

                @BeforeEach
                void setUp() {
                    String username = "username";
                    String password = "password";

                    User user = TestUserFactory.create(username, password, passwordEncoder);
                    userRepository.save(user);

                    Role role = Role.builder()
                            .user(user)
                            .type(RoleType.ROLE_ACTIVATE)
                            .build();
                    roleRepository.save(role);

                    activateUserJwtCredential = jwtCredentialEncoder.encode(user.getId());
                }

                @Test
                @DisplayName("200을 응답한다")
                void it_response_200() throws Exception {
                    ResultActions actions = mockMvc.perform(
                            get("/")
                                    .header("Authorization", "Bearer " + activateUserJwtCredential)
                    );

                    actions
                            .andExpect(status().isOk());
                }

            }

            @Nested
            @DisplayName("활성화되지 않은 유저일 경우")
            class Context_deactivateUser {
                private String deactivateUserJwtCredential;

                @BeforeEach
                void setUp() {
                    User user = TestUserFactory.create("username", "password", passwordEncoder);
                    userRepository.save(user);

                    Role role = Role.builder()
                            .user(user)
                            .type(RoleType.ROLE_DEACTIVATE)
                            .build();
                    roleRepository.save(role);

                    deactivateUserJwtCredential = jwtCredentialEncoder.encode(user.getId());
                }

                @Test
                @DisplayName("403을 응답한다")
                void It_response403() throws Exception {
                    ResultActions actions = mockMvc.perform(
                            get("/")
                                    .header("Authorization", "Bearer " + deactivateUserJwtCredential)
                    );

                    actions
                            .andExpect(status().isForbidden());
                }
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
                            get("/")
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
                            get("/")
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
                        get("/")
                                .header("Authorization", notAllowedJwtType + VALID_JWT_CREDENTIAL)
                );

                actions
                        .andExpect(status().isBadRequest());
            }
        }
    }
}

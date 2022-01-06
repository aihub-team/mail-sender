package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@SpringBootTest
public class UserRegisterServiceTest {

    @Autowired
    private UserRegisterService userRegisterService;

    @Nested
    @DisplayName("registerUser 메서드는")
    class Describe {

        @Nested
        @DisplayName("올바른 회원가입 요청 데이터가 주어질 때")
        class Context_validUserRegisterRequestData {
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
            @DisplayName("생성된 유저를 리턴한다")
            void it_returns_created_user() {
                assertThatCode(() -> {
                    User user = userRegisterService.registerUser(validUserRegisterRequest);

                    assertThat(user.getUsername()).isEqualTo("username");
                    assertThat(user.getPassword()).isNotEqualTo("password");
                }).doesNotThrowAnyException();
            }

        }

    }
}

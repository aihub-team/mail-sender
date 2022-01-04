package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoginServiceTest {
    private static final String JWT_CREDENTIAL_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private LoginService loginService;

    @Test
    @DisplayName("login메서드는 username이 주어지면 로그인 요청 수행 후 Jwt Credential를 리턴한다")
    void login() {
        String username = "username";

        String accessToken = loginService.login(username);

        assertThat(accessToken).matches(JWT_CREDENTIAL_REGEX);
    }
}

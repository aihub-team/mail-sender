package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(new JwtEncoder(SECRET));
    }

    @Test
    @DisplayName("login메서드는 username이 주어지면 액세스 토큰을 리턴한다")
    void login() {
        String username = "username";

        String accessToken = loginService.login(username);

        assertThat(accessToken).isEqualTo(VALID_TOKEN);
    }
}

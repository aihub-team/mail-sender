package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtCredentialsEncoderTest {
    private static final String JWT_CREDENTIALS_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private JwtCredentialsEncoder jwtCredentialsEncoder;

    @Test
    @DisplayName("encode 메서드는 암호화된 Jwt Credentials를 리턴한다")
    void encode() {
        String username = "username";

        String accessToken = jwtCredentialsEncoder.encode(username);

        assertThat(accessToken).matches(JWT_CREDENTIALS_REGEX);
    }
}

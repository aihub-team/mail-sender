package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtEncoderTest {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";


    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    @DisplayName("encode 메서드는 암호화된 액세스 토큰을 리턴한다")
    void encode() {
        String username = "username";

        String accessToken = jwtEncoder.encode(username);

        assertThat(accessToken).matches(TOKEN_REGEX);
    }
}

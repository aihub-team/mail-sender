package kr.or.aihub.mailsender.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtEncoderTest {
    private static final String SECRET = "12345678901234567890123456789012";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIn0.Lz32Q7FAltMuGgSo1GNHFKMeCP_KBSBIohDELWHJ8xM";

    private JwtEncoder jwtEncoder;

    @BeforeEach
    void setUp() {
        jwtEncoder = new JwtEncoder(SECRET);
    }

    @Test
    @DisplayName("encode 메서드는 암호화된 액세스 토큰을 리턴한다")
    void encode() {
        String accessToken = jwtEncoder.encode("username");

        assertThat(accessToken).isEqualTo(VALID_TOKEN);
    }
}

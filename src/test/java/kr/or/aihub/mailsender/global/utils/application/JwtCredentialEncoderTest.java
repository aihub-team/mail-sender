package kr.or.aihub.mailsender.global.utils.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtCredentialEncoderTest {
    private static final String JWT_CREDENTIAL_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private JwtCredentialEncoder jwtCredentialEncoder;

    @Nested
    @DisplayName("encode 메서드는")
    class Describe_encode {

        @Test
        @DisplayName("암호화된 Jwt 자격증명을 리턴한다")
        void It_returnsEncodedJwtCredential() {
            Long userId = 1L;

            String jwtCredential = jwtCredentialEncoder.encode(userId);

            assertThat(jwtCredential).matches(JWT_CREDENTIAL_REGEX);
        }
    }
}

package kr.or.aihub.mailsender.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 처리 담당.
 */
@Service
public class LoginService {
    private JwtCredentialEncoder jwtCredentialEncoder;

    public LoginService(JwtCredentialEncoder jwtCredentialEncoder) {
        this.jwtCredentialEncoder = jwtCredentialEncoder;
    }

    /**
     * 로그인 수행 후 Jwt Credentials를 리턴합니다.
     *
     * @param username 클레임에 포함될 유저이름
     * @return 클레임을 포함한 Jwt Credentials
     */
    @Transactional
    public String login(String username) {
        String jwtCredential = jwtCredentialEncoder.encode(username);

        return jwtCredential;
    }
}

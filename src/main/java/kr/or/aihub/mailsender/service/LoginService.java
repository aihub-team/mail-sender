package kr.or.aihub.mailsender.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 처리 담당.
 */
@Service
public class LoginService {
    private JwtEncoder jwtEncoder;

    public LoginService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * 클레임을 포함한 액세스 토큰을 리턴합니다.
     *
     * @param username 클레임에 포함될 유저이름
     * @return 클레임을 포함한 액세스 토큰
     */
    @Transactional
    public String login(String username) {
        String accessToken = jwtEncoder.encode(username);

        return accessToken;
    }
}

package kr.or.aihub.mailsender.domain.user;

import kr.or.aihub.mailsender.domain.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 테스트 유저 생성 담당.
 */
public class TestUserFactory {
    /**
     * 테스트 유저를 생성합니다.
     *
     * @param username        유저 이름
     * @param password        유저 비밀번호
     * @param passwordEncoder 비밀번호 암호화 구현체
     * @return 생성된 테스트 유저
     */
    public static User create(String username, String password, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }

}

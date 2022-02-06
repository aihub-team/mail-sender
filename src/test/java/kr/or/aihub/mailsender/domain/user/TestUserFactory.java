package kr.or.aihub.mailsender.domain.user;

import kr.or.aihub.mailsender.domain.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 테스트 유저 생성 담당.
 */
public class TestUserFactory {

    public static User create(PasswordEncoder passwordEncoder) {
        return User.createWithPasswordEncoder("username", "password", passwordEncoder);
    }
}

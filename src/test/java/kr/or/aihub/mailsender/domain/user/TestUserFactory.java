package kr.or.aihub.mailsender.domain.user;

import kr.or.aihub.mailsender.domain.user.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 테스트 유저 생성 담당.
 */
public class TestUserFactory {
    private static String DEFAULT_USERNAME = "username";
    private static String DEFAULT_PASSWORD = "password";
    private static PasswordEncoder DEFAULT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static User create() {
        return User.createWithPasswordEncoder(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_PASSWORD_ENCODER);
    }

    public static User create(PasswordEncoder passwordEncoder) {
        return User.createWithPasswordEncoder(DEFAULT_USERNAME, DEFAULT_PASSWORD, passwordEncoder);
    }

    public static User create(String password, PasswordEncoder passwordEncoder) {
        return User.createWithPasswordEncoder(DEFAULT_USERNAME, password, passwordEncoder);
    }
}

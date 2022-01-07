package kr.or.aihub.mailsender.domain.user;

import kr.or.aihub.mailsender.domain.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUserFactory {
    public static User create(String username, String password, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }

}

package kr.or.aihub.mailsender.domain.user.domain;

import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    protected User() {
    }

    @Builder
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 비밀번호가 암호화된 유저를 리턴합니다.
     *
     * @return 비밀번호가 암호화된 유저
     */
    public static User createWithPasswordEncoder(String username, String password, PasswordEncoder passwordEncoder) {
        String encodePassword = passwordEncoder.encode(password);

        return User.builder()
                .username(username)
                .password(encodePassword)
                .build();
    }

    public static User createWithPasswordEncoder(UserRegisterRequest userRegisterRequest, PasswordEncoder passwordEncoder) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();

        return createWithPasswordEncoder(username, password, passwordEncoder);
    }
}

package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입에 필요한 데이터를 받아 생성한 유저한 유저를 리턴합니다.
     *
     * @param userRegisterRequest 회원가입 시 필요 데이터
     * @return 생성한 유저
     */
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .build();

        return userRepository.save(user);
    }
}

package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import kr.or.aihub.mailsender.domain.user.error.ExistUsernameException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 유저 회원가입 처리 담당.
 */
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
     * @throws ExistUsernameException 유저이름이 존재할 경우
     */
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();

        checkExist(username);

        String password = userRegisterRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .build();

        return userRepository.save(user);
    }

    /**
     * 유저 이름이 존재하는지 확인합니다.
     * @param username 유저 이름
     * @throws ExistUsernameException 유저이름이 존재할 경우
     */
    private void checkExist(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new ExistUsernameException(user.getUsername());
                });
    }
}

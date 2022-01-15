package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import kr.or.aihub.mailsender.domain.user.error.ConfirmPasswordNotMatchException;
import kr.or.aihub.mailsender.domain.user.error.ExistUsernameException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 유저 회원가입 처리 담당.
 */
@Service
public class UserRegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserRegisterService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * 회원가입에 필요한 데이터를 받아 생성한 유저한 유저를 리턴합니다.
     *
     * @param userRegisterRequest 회원가입 시 필요 데이터
     * @return 생성한 유저
     * @throws ExistUsernameException           유저이름이 존재할 경우
     * @throws ConfirmPasswordNotMatchException 비밀번호 확인이 틀릴 경우
     */
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        checkMatch(password, confirmPassword);

        String username = userRegisterRequest.getUsername();
        checkExist(username);

        User user = createUser(username, password);
        userRepository.save(user);

        Role role = createRole(user);
        roleRepository.save(role);

        return user;
    }

    private User createUser(String username, String password) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }

    private Role createRole(User user) {
        return Role.builder()
                .user(user)
                .type(RoleType.ROLE_DEACTIVATE)
                .build();
    }

    /**
     * 비밀번호 확인이 일치한지 검사합니다.
     *
     * @param password        비밀번호
     * @param confirmPassword 비밀번호 확인
     * @throws ConfirmPasswordNotMatchException 비밀번호 확인이 틀릴 경우
     */
    private void checkMatch(String password, String confirmPassword) {
        if (!Objects.equals(password, confirmPassword)) {
            throw new ConfirmPasswordNotMatchException();
        }
    }

    /**
     * 유저 이름이 존재하는지 확인합니다.
     *
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

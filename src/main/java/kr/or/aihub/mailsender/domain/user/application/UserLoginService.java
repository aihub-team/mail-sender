package kr.or.aihub.mailsender.domain.user.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.error.DeactivateUserException;
import kr.or.aihub.mailsender.domain.user.error.PasswordNotMatchException;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 유저 로그인 처리 담당.
 */
@Service
public class UserLoginService {
    private final UserRepository userRepository;
    private final JwtCredentialEncoder jwtCredentialEncoder;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserLoginService(
            UserRepository userRepository,
            JwtCredentialEncoder jwtCredentialEncoder,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.jwtCredentialEncoder = jwtCredentialEncoder;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * 로그인이 가능한 회원인지 검사 후 Jwt 자격증명을 리턴합니다.
     *
     * @param userLoginRequest 로그인 시 필요 데이터
     * @return 클레임을 포함한 Jwt Credential
     * @throws UserNotFoundException     유저를 찾지 못한 경우
     * @throws PasswordNotMatchException 비밀번호가 일치하지 않는 경우
     * @throws DeactivateUserException   활성화되지 않은 유저일 경우
     */
    @Transactional(readOnly = true)
    public String login(UserLoginRequest userLoginRequest) {
        String username = userLoginRequest.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        String password = userLoginRequest.getPassword();
        String userPassword = user.getPassword();

        boolean passwordMatch = passwordEncoder.matches(password, userPassword);
        if (!passwordMatch) {
            throw new PasswordNotMatchException(username);
        }

        checkActivate(user);

        return jwtCredentialEncoder.encode(user.getId());
    }

    /**
     * 활성화된 유저인지 확인합니다.
     *
     * @param user 유저
     * @throws DeactivateUserException 활성화되지 않은 유저일 경우
     */
    private void checkActivate(User user) {
        boolean isActivateUser = roleRepository.findAllByUser(user).stream()
                .anyMatch(role -> isActivate(role));

        if (!isActivateUser) {
            throw new DeactivateUserException(user.getUsername());
        }
    }

    private boolean isActivate(Role role) {
        String roleType = role.getType().toString();

        return "ROLE_ACTIVATE".equals(roleType);
    }

}

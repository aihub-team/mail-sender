package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 권한 저장 담당.
 */
@Service
public class RoleSaver {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleSaver(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 식별자로 유저를 찾아 권한 생성 및 저장 후 리턴합니다.
     *
     * @param userId   식별자
     * @param roleType 권한
     * @return 저장된 권한
     * @throws UserNotFoundException 식별자로 유저를 찾지 못한 경우
     */
    public Role createAndSave(Long userId, RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role role = Role.create(user, roleType);

        return roleRepository.save(role);
    }
}

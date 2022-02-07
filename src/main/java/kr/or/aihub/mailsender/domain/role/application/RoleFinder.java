package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleFinder {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleFinder(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * 식별자로 찾은 권한 목록을 리턴합니다.
     *
     * @param userId 식별자
     * @return 찾은 권한 목록
     * @throws UserNotFoundException 식별자로 유저를 찾지 못한 경우
     */
    public List<Role> findBy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return roleRepository.findAllByUser(user);
    }
}

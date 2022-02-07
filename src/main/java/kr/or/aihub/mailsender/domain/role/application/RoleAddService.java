package kr.or.aihub.mailsender.domain.role.application;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.role.dto.RoleAddRequest;
import kr.or.aihub.mailsender.domain.role.errors.AlreadyGrantedRoleException;
import kr.or.aihub.mailsender.domain.user.error.DeactivateUserException;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 권한 추가 담당.
 */
@Service
public class RoleAddService {
    private final RoleFinder roleFinder;
    private final RoleSaver roleSaver;

    public RoleAddService(
            RoleFinder roleFinder,
            RoleSaver roleSaver
    ) {
        this.roleFinder = roleFinder;
        this.roleSaver = roleSaver;
    }

    /**
     * 식별자를 통해 유저를 찾아 권한을 추가하고, 추가된 권한을 리턴합니다.
     *
     * @param userId         유저 식별자
     * @param roleAddRequest 추가할 권한
     * @return 추가된 권한
     * @throws AlreadyGrantedRoleException 이미 권한이 부여된 경우
     * @throws DeactivateUserException     비활성화된 유저가 어드민 권한을 요청한 경우
     * @throws UserNotFoundException       식별자로 유저를 찾지 못한 경우
     */
    public RoleType add(Long userId, RoleAddRequest roleAddRequest) {
        List<Role> userRoles = roleFinder.findBy(userId);
        RoleType requestRoleType = roleAddRequest.getRoleType();

        checkAlreadyGranted(userRoles, requestRoleType);
        checkDeactivateUserRequestAdminRole(userRoles, requestRoleType);

        Role role = roleSaver.createAndSave(userId, requestRoleType);

        return role.getType();
    }

    /**
     * 비활성화된 유저가 어드민 권한을 요청하는지 확인합니다.
     *
     * @param userRoles       기존 유저 권한
     * @param requestRoleType 요청한 권한
     * @throws DeactivateUserException 비활성화된 유저가 어드민 권한을 요청한 경우
     */
    private void checkDeactivateUserRequestAdminRole(List<Role> userRoles, RoleType requestRoleType) {
        if (!isActivateUser(userRoles) && requestRoleType.isAdminRoleType()) {
            throw new DeactivateUserException();
        }
    }

    private boolean isActivateUser(List<Role> userRoles) {
        return userRoles.stream()
                .anyMatch(Role::isActivateType);
    }

    /**
     * 권한이 이미 부여되었는지 확인합니다.
     *
     * @param userRoles 기존 유저 권한
     * @param roleType  추가할려는 권한
     * @throws AlreadyGrantedRoleException 이미 권한이 부여된 경우
     */
    private void checkAlreadyGranted(List<Role> userRoles, RoleType roleType) {
        boolean alreadyGrantedRole = userRoles.stream()
                .map(Role::getType)
                .anyMatch(roleType::equals);

        if (alreadyGrantedRole) {
            throw new AlreadyGrantedRoleException();
        }
    }
}

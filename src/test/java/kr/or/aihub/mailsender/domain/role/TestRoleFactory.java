package kr.or.aihub.mailsender.domain.role;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.domain.User;

/**
 * 테스트 권한 생성 담당.
 */
public class TestRoleFactory {

    /**
     * 권한을 생성해 리턴합니다.
     *
     * @param user 유저
     * @param type 권한 타입
     * @return 생성된 권한
     */
    public static Role create(User user, RoleType type) {
        return Role.builder()
                .user(user)
                .type(type)
                .build();
    }
}

package kr.or.aihub.mailsender.domain.role;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleType;
import kr.or.aihub.mailsender.domain.user.TestUserFactory;
import kr.or.aihub.mailsender.domain.user.domain.User;

public class TestRoleFactory {
    public static Role create(RoleType roleType) {
        User testUser = TestUserFactory.create();

        return Role.create(testUser, roleType);
    }
}

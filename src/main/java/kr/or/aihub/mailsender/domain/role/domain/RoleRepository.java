package kr.or.aihub.mailsender.domain.role.domain;

import kr.or.aihub.mailsender.domain.user.domain.User;

import java.util.List;

public interface RoleRepository {
    Role save(Role role);

    void deleteAll();

    List<Role> findAllByUser(User user);
}

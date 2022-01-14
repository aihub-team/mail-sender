package kr.or.aihub.mailsender.domain.role.domain;

public interface RoleRepository {
    Role save(Role role);

    void deleteAll();
}

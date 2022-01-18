package kr.or.aihub.mailsender.infra;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaRoleRepository extends RoleRepository, CrudRepository<Role, Long> {
    Role save(Role role);

    void deleteAll();

    List<Role> findAllByUser(User user);
}

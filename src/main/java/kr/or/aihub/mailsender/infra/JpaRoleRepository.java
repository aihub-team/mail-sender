package kr.or.aihub.mailsender.infra;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaRoleRepository extends RoleRepository, CrudRepository<Role, Long> {
    Role save(Role role);
}

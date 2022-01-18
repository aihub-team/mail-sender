package kr.or.aihub.mailsender.infra;

import kr.or.aihub.mailsender.domain.user.domain.User;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository extends UserRepository, CrudRepository<User, Long> {
    User save(User user);

    void deleteAll();

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);
}

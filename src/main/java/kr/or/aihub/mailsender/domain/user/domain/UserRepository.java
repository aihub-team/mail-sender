package kr.or.aihub.mailsender.domain.user.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    void deleteAll();

    Optional<User> findByUsername(String username);
}

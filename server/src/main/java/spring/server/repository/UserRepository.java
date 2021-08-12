package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.server.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

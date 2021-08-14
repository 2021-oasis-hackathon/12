package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.server.domain.Locker;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {
}

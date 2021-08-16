package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.server.domain.Locker;

import java.util.List;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {
    @Query("select l from Locker l where l.user.id != :userId")
    List<Locker> findAllNotMine(@Param("userId") Long userId);

    @Query("select l from Locker l where l.user.id = :userId")
    List<Locker> findByUserId(@Param("userId") Long userId);

    @Query("select l from Locker l where l.qrcode = :qrcode")
    Locker findByQrcode(@Param("qrcode") String qrcode);
}

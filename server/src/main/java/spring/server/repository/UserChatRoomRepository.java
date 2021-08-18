package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.server.domain.UserChatRoom;

@Transactional
@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Modifying
    @Query("update UserChatRoom u set u.isChecked = true where u.chatRoom.id = :roomId and u.user.id = :userId")
    void updateCheckTrue(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Modifying
    @Query("update UserChatRoom u set u.isChecked = false where u.chatRoom.id = :roomId and u.otherId = :userId")
    void updateCheckFalse(@Param("roomId") Long roomId, @Param("userId") Long userId);
}

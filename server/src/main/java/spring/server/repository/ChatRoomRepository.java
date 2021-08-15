package spring.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import spring.server.domain.ChatRoom;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select c from UserChatRoom u INNER JOIN u.chatRoom c where u.user.id = :userId")
    List<ChatRoom> findRoomByUserId(@Param("userId") Long userId);

    @Query("select distinct c from UserChatRoom u inner join u.chatRoom c where u.user.id = :userId and u.otherId = :otherId")
    ChatRoom findRoomByUsers(@Param("userId") Long userId, @Param("otherId") Long hostId);
}

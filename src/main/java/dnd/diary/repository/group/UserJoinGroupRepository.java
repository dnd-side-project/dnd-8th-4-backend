package dnd.diary.repository.group;

import dnd.diary.domain.user.UserJoinGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJoinGroupRepository extends JpaRepository<UserJoinGroup, Long> {
    @Query(value = "SELECT c.group_id FROM user_join_group AS c WHERE user_id = :user_id", nativeQuery = true)
    List<Long> findGroupIdList(@Param("user_id") Long userId);
}

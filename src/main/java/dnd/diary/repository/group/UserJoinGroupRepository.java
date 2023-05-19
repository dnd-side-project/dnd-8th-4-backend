package dnd.diary.repository.group;

import dnd.diary.domain.user.UserJoinGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJoinGroupRepository extends JpaRepository<UserJoinGroup, Long> {
    List<UserJoinGroup> findByUserId(Long userId);
    UserJoinGroup findUserJoinGroupByUserIdAndGroupId(Long userId, Long groupId);
}

package dnd.diary.repository.group;

import dnd.diary.domain.user.UserJoinGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJoinGroupRepository extends JpaRepository<UserJoinGroup, Long> {
}

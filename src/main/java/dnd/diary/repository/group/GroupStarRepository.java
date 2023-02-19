package dnd.diary.repository.group;

import dnd.diary.domain.group.GroupStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupStarRepository extends JpaRepository<GroupStar, Long> {

	GroupStar findByGroupIdAndUserId(Long groupId, Long userId);
}

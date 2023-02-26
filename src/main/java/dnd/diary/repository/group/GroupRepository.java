package dnd.diary.repository.group;

import java.util.List;

import dnd.diary.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
	Boolean existsByGroupName(String groupName);
	List<Group> findByGroupNameContainingIgnoreCaseOrGroupNoteContainingIgnoreCase(String groupName, String groupNote);
}

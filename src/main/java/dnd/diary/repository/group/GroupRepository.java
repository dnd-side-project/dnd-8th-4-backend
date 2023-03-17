package dnd.diary.repository.group;

import java.util.List;
import java.util.Optional;

import dnd.diary.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
	Boolean existsByGroupName(String groupName);
	List<Group> findByGroupNameContainingIgnoreCaseOrGroupNoteContainingIgnoreCaseAndDeletedYn(String groupName, String groupNote, boolean deletedYn);
	Optional<Group> findByIdAndDeletedYn(Long groupId, boolean deletedYn);
}

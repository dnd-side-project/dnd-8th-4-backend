package dnd.diary.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dnd.diary.domain.group.GroupImage;

@Repository
public interface GroupImageRepository extends JpaRepository<GroupImage, Long> {
}

package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
}

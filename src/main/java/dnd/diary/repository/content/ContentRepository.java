package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
    Optional<Content> findByIdAndUserId(Long contentId,Long userId);
}

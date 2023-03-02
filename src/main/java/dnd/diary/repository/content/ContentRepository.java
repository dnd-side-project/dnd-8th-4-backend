package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByIdAndUserId(Long contentId, Long userId);
    Page<Content> findByGroupId(Long groupId, Pageable pageable);
    Page<Content> findByGroupIdIn(List<Long> groupId, Pageable pageable);
    List<Content> findByIdIn(List<Long> contentId);
    Page<Content> findByIdIn(List<Long> contentId, Pageable pageable);
    Page<Content> findByUserId(Long userId, Pageable pageable);
    Page<Content> findByContentContainingAndGroupIdIn(String word, List<Long> groupId, Pageable pageable);
    List<Content> findByLocationAndGroupIdIn(String location, List<Long> groupId);
    Long countByLocation(String location);
}

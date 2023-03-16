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

    boolean existsByIdAndDeletedYn(Long contentId, Boolean deletedYn);
    Optional<Content> findByIdAndUserIdAndDeletedYn(Long contentId, Long userId, Boolean deleteYn);
    Page<Content> findByGroupIdAndDeletedYn(Long groupId, Boolean deleteYn, Pageable pageable);
    Page<Content> findByGroupIdInAndDeletedYn(List<Long> groupId, Boolean deletedYn, Pageable pageable);
    List<Content> findByIdInAndDeletedYn(List<Long> contentId, Boolean deletedYn);
    Page<Content> findByIdInAndDeletedYn(List<Long> contentId, Boolean deletedYn, Pageable pageable);
    Page<Content> findByUserIdAndDeletedYn(Long userId, Boolean deletedYn, Pageable pageable);
    Page<Content> findByContentContainingAndGroupIdInAndDeletedYn(String word, List<Long> groupId, Boolean deletedYn, Pageable pageable);
    List<Content> findByLocationAndGroupIdInAndDeletedYn(String location, List<Long> groupId, Boolean deletedYn);
    Long countByLocationAndGroupIdInAndDeletedYn(String location, List<Long> groupId, Boolean deletedYn);
}

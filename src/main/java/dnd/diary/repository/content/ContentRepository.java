package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "SELECT * FROM content AS c \n" +
            "WHERE c.group_id IN (?1) AND c.latitude between ?3 and ?2 and c.longitude between ?4 and ?5",
            nativeQuery = true)
    List<Content> findByMapList(List<Long> group_id, Double endLatitude, Double startLatitude, Double startLongitude, Double endLongitude);

    Optional<Content> findByIdAndDeletedYn(Long contentId, Boolean deletedYn);
}

package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {

    Boolean existsByIdAndDeletedYn(Long contentId, Boolean deletedYn);

    Optional<Content> findByIdAndUserIdAndDeletedYn(Long contentId, Long userId, Boolean deleteYn);

    Page<Content> findByGroupIdAndDeletedYn(Long groupId, Boolean deleteYn, Pageable pageable);

    Page<Content> findByGroupIdInAndDeletedYn(List<Long> groupId, Boolean deletedYn, Pageable pageable);

    Page<Content> findByIdInAndDeletedYn(List<Long> contentId, Boolean deletedYn, Pageable pageable);

    Page<Content> findByUserIdAndDeletedYn(Long userId, Boolean deletedYn, Pageable pageable);

    List<Content> findByLocationAndGroupIdInAndDeletedYn(String location, List<Long> groupId, Boolean deletedYn);

    Long countByLocationAndGroupIdInAndDeletedYn(String location, List<Long> groupId, Boolean deletedYn);

    @Query("SELECT c FROM Content c WHERE c.group.id IN :groupIds AND c.latitude BETWEEN :startLatitude AND :endLatitude AND c.longitude BETWEEN :startLongitude AND :endLongitude")
    List<Content> findByMapList(
            @Param("groupIds") List<Long> groupIds, @Param("endLatitude") Double endLatitude,
            @Param("startLatitude") Double startLatitude, @Param("startLongitude") Double startLongitude,
            @Param("endLongitude") Double endLongitude
    );

    Optional<Content> findByIdAndDeletedYn(Long contentId, Boolean deletedYn);
}

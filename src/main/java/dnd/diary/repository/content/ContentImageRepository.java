package dnd.diary.repository.content;

import dnd.diary.domain.content.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
    @Query(value = "SELECT c.image_name FROM content_image AS c WHERE content_id = :content_id", nativeQuery = true)
    List<String> findImageNameList(@Param("content_id") Long contentId);
    List<ContentImage> findByContentId(Long contentId);
    Optional<ContentImage> findByImageName(String imageName);
}

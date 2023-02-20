package dnd.diary.repository.content;

import dnd.diary.domain.content.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
    List<ContentImage> findByContentId(Long contentId);
    Optional<ContentImage> findByImageName(String imageName);
}

package dnd.diary.repository.content;

import dnd.diary.domain.content.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
    List<ContentImage> findByContentId(Long contentId);
}

package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import dnd.diary.response.content.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentCustomRepository {
    Page<Content> searchMyCommentPosts(Long userId, PageRequest pageRequest);
    List<Long> findContentIdList(Long userId);
    Page<Content> searchMyGroupContent(String word, List<Long> groupId, Pageable pageable);
    List<Content> mapSearchMyGroupContent(
            Double endLatitude, Double startLatitude,
            Double startLongitude, Double endLongitude, Long userId
    );
    Long countDuplicateLocation(String location, Long userId);
}

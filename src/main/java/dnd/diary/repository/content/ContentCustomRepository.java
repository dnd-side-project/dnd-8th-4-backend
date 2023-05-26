package dnd.diary.repository.content;

import dnd.diary.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ContentCustomRepository {
    Page<Content> searchMyCommentPosts(Long userId, PageRequest pageRequest);
}

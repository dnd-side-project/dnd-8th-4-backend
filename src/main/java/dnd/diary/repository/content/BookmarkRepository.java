package dnd.diary.repository.content;

import dnd.diary.domain.bookmark.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query(value = "select b.content_id from bookmark b where b.user_id = :uid", nativeQuery = true)
    List<Long> findContentIdList(@Param("uid") Long userId);
    Bookmark findByUserIdAndContentId(Long userId, Long contentId);
}

package dnd.diary.repository.content;

import dnd.diary.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT DISTINCT content_id FROM comment as c WHERE c.user_id = :userId", nativeQuery = true)
    List<Long> findDistinctContentIdListByUserId(@Param("userId") Long userId);
    Page<Comment> findByContentId(Long contentId, Pageable pageable);
}

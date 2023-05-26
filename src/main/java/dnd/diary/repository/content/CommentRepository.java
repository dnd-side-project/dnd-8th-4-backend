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
    Page<Comment> findByContentIdAndDeletedYn(Long contentId, boolean deletedYn, Pageable pageable);
    Boolean existsByIdAndDeletedYn(Long commentId, boolean deletedYn);
    Comment findCommentByIdAndDeletedYn(Long commentId, boolean deletedYn);
}

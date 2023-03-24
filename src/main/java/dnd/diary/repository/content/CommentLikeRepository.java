package dnd.diary.repository.content;

import dnd.diary.domain.comment.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByCommentIdAndUserId(Long commentId, Long userId);
    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLike findCommentLikeByCommentIdAndUserIdAndCommentLikeYn(Long commentId, Long userId, boolean commentLikeYn);
    Boolean existsByCommentIdAndUserIdAndCommentLikeYn(Long commentId, Long userId, boolean commentLikeYn);
}

package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.user.User;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.group.NotificationRepository;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.CommentLikeRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.response.CustomResponseEntity;
import dnd.diary.response.content.CommentLikeResponse;
import dnd.diary.service.group.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Transactional
    public CustomResponseEntity<CommentLikeResponse> commentLikeSave(
            Long userId, Long commentId
    ) {
        validateCommentLikeSave(commentId);

        User user = getUser(userId);
        CommentLike existsLike =
                commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());

        // 최초 좋아요 등록일 경우
        if (existsLike == null) {
            Comment targetComment = getComment(commentId);
            CommentLike commentLike = CommentLike.builder()
                    .comment(targetComment)
                    .user(user)
                    .commentLikeYn(true)
                    .build();
            commentLikeRepository.save(commentLike);

            // 자신의 댓글이 아닌 경우에 댓글 좋아요 알림 추가
            notificationService.sendToNotification(user, targetComment, commentLike);

            return CustomResponseEntity.success(CommentLikeResponse.response(commentLike));

            // 최초 등록이 아닐 경우
        } else {

            // 댓글 좋아요 취소일 경우
            if (existsLike.isCommentLikeYn()) {
                existsLike.cancelCommentLike();
                return CustomResponseEntity.successDeleteLike();
            }

            // 댓글 좋아요 취소 후 다시 등록할 경우
            else {
                existsLike.addCommentLike();
                return CustomResponseEntity.success(
                        CommentLikeResponse.response(existsLike)
                );
            }
        }
    }

    // method
    private Comment getComment(Long commentId) {
        Comment comment = commentRepository.findCommentByIdAndDeletedYn(commentId, false);
        if (comment == null) {
            throw new CustomException(Result.FAIL);
        } else {
            return comment;
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_USER)
                );
    }

    // validate
    private void validateCommentLikeSave(Long commentId) {
        if (!commentRepository.existsByIdAndDeletedYn(commentId, false)) {
            throw new CustomException(Result.NOT_FOUND_COMMENT);
        }
    }
}

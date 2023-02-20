package dnd.diary.service.content;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.user.User;
import dnd.diary.dto.content.CommentLikeDto;
import dnd.diary.enumeration.Result;
import dnd.diary.exception.CustomException;
import dnd.diary.repository.user.UserRepository;
import dnd.diary.repository.content.CommentLikeRepository;
import dnd.diary.repository.content.CommentRepository;
import dnd.diary.response.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CustomResponseEntity<CommentLikeDto.SaveCommentLike> commentSave(
            UserDetails userDetails, Long commentId
    ) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        User user = userRepository.findOneWithAuthoritiesByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new CustomException(Result.FAIL)
                );

        CommentLike existsLike = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (existsLike == null){
            return CustomResponseEntity.success(
                    CommentLikeDto.SaveCommentLike.response(
                            commentLikeRepository.save(
                                    CommentLike.builder()
                                            .comment(comment)
                                            .user(user)
                                            .build()
                            )
                    )
            );
        } else {
            commentLikeRepository.deleteById(existsLike.getId());
            return CustomResponseEntity.successDeleteLike();
        }

    }
}

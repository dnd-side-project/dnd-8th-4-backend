package dnd.diary.response.content;

import dnd.diary.domain.comment.CommentLike;
import lombok.*;

@NoArgsConstructor
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommentLikeResponse {
    private Long id;
    private Long commentId;
    private Long userId;

    public static CommentLikeResponse response(CommentLike commentLike) {
        return CommentLikeResponse.builder()
                .id(commentLike.getId())
                .commentId(commentLike.getComment().getId())
                .userId(commentLike.getUser().getId())
                .build();
    }
}

package dnd.diary.dto.content;


import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.comment.CommentLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentLikeDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class SaveCommentLike {
        private Long id;
        private Long commentId;
        private Long userId;

        public static CommentLikeDto.SaveCommentLike response(CommentLike commentLike) {
            return SaveCommentLike.builder()
                    .id(commentLike.getId())
                    .commentId(commentLike.getComment().getId())
                    .userId(commentLike.getUser().getId())
                    .build();
        }
    }
}

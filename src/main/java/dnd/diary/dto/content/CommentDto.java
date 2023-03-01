package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CommentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class AddCommentDto {

        private Long id;
        private String commentNote;
        private Long userId;
        private Long contentId;
        private Long stickerId;

        public static CommentDto.AddCommentDto response(Comment comment) {
            return AddCommentDto.builder()
                    .id(comment.getId())
                    .commentNote(comment.getCommentNote())
                    .userId(comment.getUser().getId())
                    .contentId(comment.getContent().getId())
                    .stickerId(null)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class pageCommentDto {

        private Long id;
        private String commentNote;
        private String profileImageUrl;
        private String username;
        private LocalDateTime createdAt;
        private Boolean likesExists;

        public static CommentDto.pageCommentDto response(Comment comment, Boolean likesExists) {
            return pageCommentDto.builder()
                    .id(comment.getId())
                    .commentNote(comment.getCommentNote())
                    .profileImageUrl(comment.getUser().getProfileImageUrl())
                    .username(comment.getUser().getNickName())
                    .createdAt(comment.getCreatedAt())
                    .likesExists(likesExists)
                    .build();
        }
    }
}

package dnd.diary.response.content;

import dnd.diary.domain.comment.Comment;
import lombok.*;

import java.time.LocalDateTime;

public class CommentResponse {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Add {

        private Long id;
        private String commentNote;
        private Long userId;
        private Long contentId;
        private Long stickerId;

        public static CommentResponse.Add response(Comment comment) {
            Long stickerId = (comment.getSticker() != null) ? comment.getSticker().getId() : null;
            return CommentResponse.Add.builder()
                    .id(comment.getId())
                    .commentNote(comment.getCommentNote())
                    .userId(comment.getUser().getId())
                    .contentId(comment.getContent().getId())
                    .stickerId(stickerId)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Detail {

        private Long id;
        private Long userId;
        private String stickerImageUrl;
        private String commentNote;
        private String profileImageUrl;
        private String userName;
        private LocalDateTime createdAt;
        private Boolean likesExists;

        public static CommentResponse.Detail response(Comment comment, Boolean likesExists) {
            String sticker = (comment.getSticker() != null) ? comment.getSticker().getStickerImageUrl() : null;
            return CommentResponse.Detail.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .commentNote(comment.getCommentNote())
                    .stickerImageUrl(sticker)
                    .profileImageUrl(comment.getUser().getProfileImageUrl())
                    .userName(comment.getUser().getNickName())
                    .createdAt(comment.getCreatedAt())
                    .likesExists(likesExists)
                    .build();
        }
    }
}

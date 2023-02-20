package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.Sticker;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

        public static CommentDto.AddCommentDto response(Comment comment){
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class pageCommentDto {

        private Long id;
        private String commentNote;
        private String username;
        private LocalDateTime createdAt;
        private Boolean likesExists;

        public static CommentDto.pageCommentDto response(Comment comment, Boolean likesExists){
            return pageCommentDto.builder()
                    .id(comment.getId())
                    .commentNote(comment.getCommentNote())
                    .username(comment.getUser().getNickName())
                    .createdAt(comment.getCreatedAt())
                    .likesExists(likesExists)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class pagePostsCommentDto {

        private Long emotions;
        private Long comments;
        private List<ContentDto.EmotionResponseDto> emotionList;
        private Page<pageCommentDto> page;

        public static CommentDto.pagePostsCommentDto response(Page<pageCommentDto> page, List<ContentDto.EmotionResponseDto> emotionList, Long emotions, Long comments){
            return pagePostsCommentDto.builder()
                    .emotions(emotions)
                    .comments(comments)
                    .emotionList(emotionList)
                    .page(page)
                    .build();
        }
    }
}

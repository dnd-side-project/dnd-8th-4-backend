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

import javax.persistence.*;
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
}

package dnd.diary.request.controller.content;

import dnd.diary.request.service.content.CommentServiceRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentRequest {

    @NoArgsConstructor
    @Getter
    public static class Add {

        @NotBlank(message = "댓글 내용은 필수입니다.")
        private String commentNote;
        private Long stickerId;

        public CommentServiceRequest.Add toServiceRequest() {
            return CommentServiceRequest.Add.builder()
                    .commentNote(commentNote)
                    .stickerId(stickerId)
                    .build();
        }

        // 테스트 생성자
        public Add(String commentNote, Long stickerId) {
            this.commentNote = commentNote;
            this.stickerId = stickerId;
        }
    }
}

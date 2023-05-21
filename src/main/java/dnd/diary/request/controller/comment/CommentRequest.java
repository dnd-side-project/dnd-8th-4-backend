package dnd.diary.request.controller.comment;

import dnd.diary.request.service.CommentServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequest {

    @NoArgsConstructor
    @Getter
    public static class Add {
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

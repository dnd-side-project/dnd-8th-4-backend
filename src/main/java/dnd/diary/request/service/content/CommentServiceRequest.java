package dnd.diary.request.service.content;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentServiceRequest {

    @NoArgsConstructor
    @Getter
    public static class Add {
        private String commentNote;
        private Long stickerId;

        @Builder
        private Add(String commentNote, Long stickerId) {
            this.commentNote = commentNote;
            this.stickerId = stickerId;
        }
    }
}

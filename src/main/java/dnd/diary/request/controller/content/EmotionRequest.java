package dnd.diary.request.controller.content;

import dnd.diary.request.service.content.CommentServiceRequest;
import dnd.diary.request.service.content.EmotionServiceRequest;
import dnd.diary.service.content.CommentService;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class EmotionRequest {

    @NoArgsConstructor
    @Getter
    public static class Add {
        @NotNull(message = "공감 상태를 입력받지 못했습니다.")
        private Long emotionStatus;

        public EmotionServiceRequest.Add toServiceRequest() {
            return EmotionServiceRequest.Add.builder()
                    .emotionStatus(emotionStatus)
                    .build();
        }
    }
}

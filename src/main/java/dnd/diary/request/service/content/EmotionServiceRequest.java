package dnd.diary.request.service.content;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class EmotionServiceRequest {
    @NoArgsConstructor
    @Getter
    public static class Add {
        @NotNull(message = "공감 상태를 입력받지 못했습니다.")
        private Long emotionStatus;

        @Builder
        private Add(Long emotionStatus) {
            this.emotionStatus = emotionStatus;
        }
    }
}

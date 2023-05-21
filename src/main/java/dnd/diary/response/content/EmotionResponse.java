package dnd.diary.response.content;

import dnd.diary.domain.content.Emotion;
import lombok.*;

public class EmotionResponse {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Add {
        private Long id;
        private Long emotionStatus;
        private Long contentId;
        private Long userId;
        private Boolean emotionYn;

        public static EmotionResponse.Add response(Emotion emotion) {
            return Add.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .contentId(emotion.getContent().getId())
                    .userId(emotion.getUser().getId())
                    .emotionYn(true)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Detail {
        private Long id;
        private Long emotionStatus;

        public static EmotionResponse.Detail response(Emotion emotion) {
            return EmotionResponse.Detail.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .build();
        }
    }
}

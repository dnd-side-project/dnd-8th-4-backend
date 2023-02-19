package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EmotionDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class AddEmotionDto{
        private Long id;
        private Long emotionStatus;
        private Long contentId;
        private Long userId;

        public static EmotionDto.AddEmotionDto response(Emotion emotion){
            return AddEmotionDto.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .contentId(emotion.getContent().getId())
                    .userId(emotion.getUser().getId())
                    .build();
        }
    }
}

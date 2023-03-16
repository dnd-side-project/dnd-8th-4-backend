package dnd.diary.dto.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class EmotionDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class AddEmotionDto{
        private Long id;
        @NotNull(message = "공감 상태를 입력받지 못했습니다.")
        private Long emotionStatus;
        private Long contentId;
        private Long userId;
        private Boolean emotionYn;

        public static EmotionDto.AddEmotionDto response(Emotion emotion){
            return AddEmotionDto.builder()
                    .id(emotion.getId())
                    .emotionStatus(emotion.getEmotionStatus())
                    .contentId(emotion.getContent().getId())
                    .userId(emotion.getUser().getId())
                    .emotionYn(true)
                    .build();
        }
    }
}

package dnd.diary.dto.mission;

import dnd.diary.dto.content.ContentDto;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class MissionCheckContentRequest {

    private Long missionId;

    @NotNull(message = "내용을 입력하지 않았습니다.")
    private String content;
}
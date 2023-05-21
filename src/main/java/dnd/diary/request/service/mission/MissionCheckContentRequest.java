package dnd.diary.request.service.mission;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class MissionCheckContentRequest {

    private Long missionId;

    @NotNull(message = "내용을 입력하지 않았습니다.")
    private String content;
}
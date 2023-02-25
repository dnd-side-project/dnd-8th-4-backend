package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionCheckContentResponse {

    private Long missionId;
    private Boolean locationCheck;
    private Boolean contentCheck;
    private Boolean isComplete;

}

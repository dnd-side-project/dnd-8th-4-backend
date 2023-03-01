package dnd.diary.response.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionCheckLocationResponse {

    private Long missionId;
    private Integer distance;
    private Boolean locationCheck;
    private Boolean contentCheck;
    private Boolean isComplete;

}

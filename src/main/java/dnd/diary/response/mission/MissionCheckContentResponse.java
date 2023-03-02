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

    private Boolean isGetNewSticker;   // 새로운 스티커를 달성했는지 여부
    private Long currMainLevel;   // 현재의 업그레이드 된 main 레벨
    private Long getNewStickerGroupId;   // 그때의 새로운 스티커 그룹 ID
    private String getNewStickerGroupName;   // 그때의 새로운 스티커 그룹 이름

}

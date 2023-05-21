package dnd.diary.request.service.mission;

import lombok.Getter;

@Getter
public class MissionCheckLocationRequest {

    private Long missionId;
    private Long groupId;

    private Double currLatitude;   // 현재 위치 위도
    private Double currLongitude;   // 현재 위치 경도
}

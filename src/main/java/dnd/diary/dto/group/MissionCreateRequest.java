package dnd.diary.dto.group;

import java.time.LocalDateTime;

import dnd.diary.domain.mission.MissionStatus;
import lombok.Getter;

@Getter
public class MissionCreateRequest {

	private String missionName;   // 미션 이름
	private String missionNote;   // 미션 내용

	private Long createUserId;   // 미션 생성자 ID
	private Long groupId;   // 미션이 생성된 ID

	private LocalDateTime missionStartDate;   // 미션 시작일
	private LocalDateTime missionEndDate;   // 미션 종료일
	private MissionStatus missionStatus;   // 미션 기간에 따른 상태

	private String missionLocationName;   // 미션 위치 이름
	private Double latitude;   // 미션 위치 위도
	private Double longitude;   // 미션 위치 경도
}

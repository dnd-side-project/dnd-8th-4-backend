package dnd.diary.dto.group;

import java.time.LocalDate;
import java.time.LocalDateTime;

import dnd.diary.domain.mission.MissionStatus;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class MissionCreateRequest {

	private String missionName;   // 미션 이름
	private String missionNote;   // 미션 내용

	private Long groupId;   // 미션이 생성된 ID

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate missionStartDate;   // 미션 시작일
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate missionEndDate;   // 미션 종료일

	private String missionLocationName;   // 미션 위치 이름
	private Double latitude;   // 미션 위치 위도
	private Double longitude;   // 미션 위치 경도
}

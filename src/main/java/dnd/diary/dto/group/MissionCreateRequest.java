package dnd.diary.dto.group;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class MissionCreateRequest {

	private String missionName;   // 미션 이름
	private String missionNote;   // 미션 내용

	private Long groupId;   // 미션이 생성된 ID

	private Boolean existPeriod;   // 미션 설정 기간 설정 여부
	@JsonFormat(pattern = "yyyy.MM.dd")
	private LocalDate missionStartDate;   // 미션 시작일
	@JsonFormat(pattern = "yyyy.MM.dd")
	private LocalDate missionEndDate;   // 미션 종료일

	private String missionLocationName;   // 미션 위치 이름
	private String missionLocationAddress;   // 미션 위치 주소
	private Double latitude;   // 미션 위치 위도
	private Double longitude;   // 미션 위치 경도

	private Integer missionColor;   // 미션 색상
}

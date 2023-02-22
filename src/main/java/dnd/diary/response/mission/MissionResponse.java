package dnd.diary.response.mission;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import dnd.diary.domain.mission.MissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {

	private Long missionId;   // 미션 ID
	private String missionName;   // 미션 이름
	private String missionNote;   // 미션 내용

	private Long createUserId;   // 미션 생성자 ID
	private Long groupId;   // 미션이 생성된 ID

	private Boolean existPeriod;
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime missionStartDate;   // 미션 시작일
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime missionEndDate;   // 미션 종료일
	private MissionStatus missionStatus;   // 미션 기간에 따른 상태

	private String missionLocationName;   // 미션 위치 이름
	private Double latitude;   // 미션 위치 위도
	private Double longitude;   // 미션 위치 경도

	private Boolean locationCheck;   // 미션에 대한 위치 인증 여부
	private Boolean contentCheck;   // 미션에 대한 글쓰기 완료 여부
	private Boolean isComplete;

	private long missionDday;   // 미션 종료까지 남은 일수
	private Integer missionColor;
}

package dnd.diary.response.mission;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import dnd.diary.domain.mission.MissionStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {

	private Long missionId;   // 미션 ID
	private String missionName;   // 미션 이름
	private String missionNote;   // 미션 내용

	private Long createUserId;   // 미션 생성자 ID
	private String createUserName;   // 미션 생성자 이름
	private String createUserProfileImageUrl;   // 미션 생성자 프로필

	private Long groupId;   // 미션이 생성된 그룹 ID
	private String groupName;   // 미션이 생성된 그룹 이름
	private String groupImageUrl;   // 미션이 생성된 그룹 이미지

	private Boolean existPeriod;
	@JsonFormat(pattern = "yyyy.MM.dd")
	private LocalDateTime missionStartDate;   // 미션 시작일
	@JsonFormat(pattern = "yyyy.MM.dd")
	private LocalDateTime missionEndDate;   // 미션 종료일
	private MissionStatus missionStatus;   // 미션 기간에 따른 상태

	private String missionLocationName;   // 미션 위치 이름
	private String missionLocationAddress;   // 미션 위치 주소
	private Double latitude;   // 미션 위치 위도
	private Double longitude;   // 미션 위치 경도

	private Long missionDday;   // 미션 종료까지 남은 일수
	private Integer missionColor;

	// 조회 유저의 해당 미션에 대한 달성 현황
	@Setter
	private UserAssignMissionInfo userAssignMissionInfo;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserAssignMissionInfo {
		private Long userId;   // 미션을 할당받은 유저 ID
		private String userNickname;   // 미션을 할당받은 유저 닉네임
		private Long missionId;   // 할당받은 미션 ID
		private Boolean locationCheck;   // 미션에 대한 위치 인증 여부
		private Boolean contentCheck;   // 미션에 대한 글쓰기 완료 여부
		private Boolean isComplete;   // 미션 전체 완료 여부
	}
}

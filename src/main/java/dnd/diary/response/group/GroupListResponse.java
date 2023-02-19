package dnd.diary.response.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListResponse {
	private Boolean existGroup;   // 가입한 그룹이 있는지 여부
	private Long groupId;   // 그룹 ID
	private String groupName;   // 그룹 이름
	private String groupNote;   // 그룹 소개
	@JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
	private LocalDateTime groupCreatedAt;   // 그룹 생성일
	private int memberCount;   // 그룹 인원
	private Boolean isStarGroup;   // 조회 유저가 해당 그룹을 즐겨찾기 했는지 여부
}

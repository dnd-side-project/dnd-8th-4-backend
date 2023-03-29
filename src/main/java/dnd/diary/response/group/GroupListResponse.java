package dnd.diary.response.group;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListResponse {

	private boolean isNewNotification;
	private Boolean existGroup;   // 가입한 그룹이 있는지 여부
	private List<GroupInfo> groupInfoList = new ArrayList<>();

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GroupInfo {
		private Long groupId;   // 그룹 ID
		private String groupName;   // 그룹 이름
		private String groupNote;   // 그룹 소개
		private String groupImageUrl;   // 그룹 이미지
		@JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
		private LocalDateTime groupCreatedAt;   // 그룹 생성일
		@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime recentUpdatedAt;   // 게시물 최신 등록일
		private int memberCount;   // 그룹 인원
		private Boolean isStarGroup;   // 조회 유저가 해당 그룹을 즐겨찾기 했는지 여부
	}
}

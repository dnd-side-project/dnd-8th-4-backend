package dnd.diary.response.group;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
	private Long groupId;
	private String groupName;
	private String groupNote;
	private String groupImageUrl;

	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime groupCreatedAt;
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime groupModifiedAt;
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime groupRecentUpdatedAt;

	private long memberCount;
	private Boolean isStarGroup;
	private Boolean isHostUser;   // 조회 유저가 해당 그룹의 방장인지 정보

	private HostUserInfo hostUserInfo;
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class HostUserInfo {
		private Long hostUserId;
		private String hostUserNickname;
		private String hostUserProfileImageUrl;
	}


	private List<GroupMemberInfo> groupMemberInfoList;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GroupMemberInfo {
		private Long userId;
		private String userName;
		private String userNickname;
		private String userEmail;
		private String userProfileImageUrl;
		@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
		private LocalDateTime userJoinGroupDatedAt;
	}
}

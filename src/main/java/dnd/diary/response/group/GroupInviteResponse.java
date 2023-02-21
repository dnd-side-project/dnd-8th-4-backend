package dnd.diary.response.group;

import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteResponse {

	private Long groupId;
	private String groupName;
	private HostUser hostUser;
	private List<InvitedUserInfo> invitedUserInfoList;
	private int successInvitedUserCount;

	@Getter
	public static class HostUser {
		private Long userId;
		private String userNickname;

		public HostUser(User user) {
			this.userId = user.getId();
			this.userNickname = user.getNickName();
		}
	}

	@Getter
	public static class InvitedUserInfo {
		private Long userId;
		private String userNickname;

		public InvitedUserInfo(User user) {
			this.userId = user.getId();
			this.userNickname = user.getNickName();
		}
	}
}

package dnd.diary.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.group.Group;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.group.NotificationType;
import dnd.diary.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentCommentNotificationResponse {

	private List<ContentCommentNotificationInfo> contentCommentNotificationInfoList;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContentCommentNotificationInfo {
		private Long notificationId;
		private NotificationType notificationType;

		private Long groupId;
		private String groupName;
		private String groupImageUrl;

		private Long contentId;
		private Long commentId;
		private String commentNote;

		// 작성자 관련
		private String userName;
		private String userProfileImageUrl;

		@JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
		private LocalDateTime createdAt;

		@Builder
		public ContentCommentNotificationInfo(Notification notification, Group group, User user, Content content, Comment comment) {
			this.notificationId = notification.getId();
			this.notificationType = notification.getNotificationType();

			this.groupId = group.getId();
			this.groupName = group.getGroupName();
			this.groupImageUrl = group.getGroupImageUrl();

			this.contentId = content.getId();

			this.commentId = comment.getId();
			this.commentNote = comment.getCommentNote();

			this.userName = user.getName();
			this.userProfileImageUrl = user.getProfileImageUrl();

			this.createdAt = comment.getCreatedAt();
		}
	}
	private long count;
}

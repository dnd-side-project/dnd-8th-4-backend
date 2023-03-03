package dnd.diary.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ContentEmotionNotificationResponse {

	private List<ContentEmotionNotificationInfo> contentEmotionNotificationInfoList;
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor

	public static class ContentEmotionNotificationInfo {
		private Long notificationId;
		private NotificationType notificationType;

		private Long groupId;
		private String groupName;
		private String groupImageUrl;

		private Long contentId;
		private Long emotionId;
		private Long emotionStatus;

		// 작성자 관련
		private String userName;
		private String userProfileImageUrl;

		@JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
		private LocalDateTime createdAt;

		@Builder
		public ContentEmotionNotificationInfo(Notification notification, Group group, User user, Content content,
			Emotion emotion) {
			this.notificationId = notification.getId();
			this.notificationType = notification.getNotificationType();

			this.groupId = group.getId();
			this.groupName = group.getGroupName();
			this.groupImageUrl = group.getGroupImageUrl();

			this.contentId = content.getId();

			this.emotionId = emotion.getId();
			this.emotionStatus = emotion.getEmotionStatus();

			this.userName = user.getName();
			this.userProfileImageUrl = user.getProfileImageUrl();

			this.createdAt = emotion.getCreatedAt();
		}
	}
	private long count;
}
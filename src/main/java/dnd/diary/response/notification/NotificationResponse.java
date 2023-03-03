package dnd.diary.response.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

	private InviteNotificationResponse inviteNotificationResponse;
	private ContentCommentNotificationResponse contentCommentNotificationResponse;
	private ContentEmotionNotificationResponse contentEmotionNotificationResponse;
	private CommentEmotionNotificationResponse commentEmotionNotificationResponse;

	// private long totalCount;

}

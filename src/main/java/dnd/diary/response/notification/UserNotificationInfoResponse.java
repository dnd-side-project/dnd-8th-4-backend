package dnd.diary.response.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 읽지 않은 알림 개수 > 0 인 경우 isNewNotification : true
 * 읽지 않은 알림 개수 = 0 인 경우 isNewNotification : false
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationInfoResponse {

	private boolean isNewNotification;
	private long noReadCount;
	private long readCount;
	private long totalCount;   // notReadCount + readCount
}

package dnd.diary.response.notification;

import dnd.diary.domain.group.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadResponse {

    private Long notificationId;   // 알림 ID
    private NotificationType notificationType;   // 알림 타입
    private boolean readYn;   // 알림 읽음 여부
}
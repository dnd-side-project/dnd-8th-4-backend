package dnd.diary.response.notification;

import dnd.diary.enumeration.NotificationType;
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
    private NotificationType notificationType;   // 알림 타입 - 초대 고정
    private NotificationInfo notificationInfo;   // 초대 알림 정보

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private Long groupId;   // 초대된 그룹 ID
        private Long groupName;   // 초대된 그룹 이름
        private boolean readYn;   // 알림 읽음 여부
    }

    private boolean readYn;   // 알림 읽음 여부
}
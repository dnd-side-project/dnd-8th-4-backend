package dnd.diary.response.notification;

import dnd.diary.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private NotificationType notificationType = NotificationType.INVITE;
    private List<NotificationInfo> notificationInfoList;

    public static class NotificationInfo {
        private Long groupId;   // 초대된 그룹 ID
        private Long groupName;   // 초대된 그룹 이름
        private boolean readYn;   // 알림 읽음 여부
    }
}

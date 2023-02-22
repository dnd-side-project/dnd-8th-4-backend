package dnd.diary.response.notification;

import dnd.diary.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private NotificationType notificationType = NotificationType.INVITE;
    private List<NotificationInfo> notificationInfoList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private Long groupId;   // 초대된 그룹 ID
        private String groupName;   // 초대된 그룹 이름
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime groupInvitedAt;   // 그룹 초대 일자
        private boolean readYn;   // 알림 읽음 여부
    }

    private long totalCount;   // 알림 전체 개수
}

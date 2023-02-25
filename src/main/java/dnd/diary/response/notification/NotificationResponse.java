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

    private List<NotificationInfo> notificationInfoList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private NotificationType notificationType = NotificationType.INVITE;
        private Long notificationId;
        private Long groupId;   // 초대된 그룹 ID
        private String groupName;   // 초대된 그룹 이름
        private String groupNote;   // 초대된 그룹 소개
        private String groupImageUrl;   // 초대된 그룹 이미지
        @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
        private LocalDateTime groupInvitedAt;   // 그룹 초대 일자
        private boolean readYn;   // 알림 읽음 여부
    }

    private long totalCount;   // 알림 전체 개수
}

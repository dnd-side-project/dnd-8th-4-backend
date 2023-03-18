package dnd.diary.response.notification;

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

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllNotificationListResponse {

    private List<NotificationInfo> notificationInfoList;

    @Getter
    public static class NotificationInfo {

        private Long notificationId;
        private NotificationType notificationType;

        private Long groupId;   // 초대된 그룹 ID / 새로운 멤버가 추가된 그룹 ID
        private String groupName;   // 초대된 그룹 이름 / 새로운 멤버가 추가된 그룹 이름
        private String groupNote;   // 초대된 그룹 소개 / 새로운 멤버가 추가된 그룹 소개
        private String groupImageUrl;   // 초대된 그룹 이미지 / 새로운 멤버가 추가된 그룹 이미지 url

        // 작성자 관련 / 그룹 새 멤버 관련
        private String userName;
        private String userProfileImageUrl;

        // 게시물 댓글 관련
        private Long contentId;
        private Long commentId;
        private String commentNote;
        private Long stickerId;

        // 게시물 공감 관련
        private Long emotionId;
        private Long emotionStatus;

        // 알림 발생 날짜 (그룹 초대 일자, 게시물 댓글 생성 일자, 게시물 공감 생성 일자)
        @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        private boolean readYn;   // 알림 읽음 여부

        // 그룹 초대 response
        @Builder
        public NotificationInfo(Notification notification) {
            this.notificationId = notification.getId();
            this.notificationType = notification.getNotificationType();

            Group invitedGroup = notification.getInvite().getGroup();
            this.groupId = invitedGroup.getId();
            this.groupName = invitedGroup.getGroupName();
            this.groupNote = invitedGroup.getGroupNote();
            this.groupImageUrl = invitedGroup.getGroupImageUrl();

            // 초대한 유저 기준
            this.userName = invitedGroup.getGroupCreateUser().getName();
            this.userProfileImageUrl = invitedGroup.getGroupCreateUser().getProfileImageUrl();

            this.createdAt = notification.getCreatedAt();
            this.readYn = notification.isReadYn();
        }

        // 내 게시물 댓글 response
        @Builder
        public NotificationInfo(Notification notification, Content content, Comment comment) {
            this.notificationId = notification.getId();
            this.notificationType = notification.getNotificationType();

            // 내 게시물이 속한 그룹 정보
            Group targetGroup = content.getGroup();
            this.groupId = targetGroup.getId();
            this.groupName = targetGroup.getGroupName();
            this.groupNote = targetGroup.getGroupNote();
            this.groupImageUrl = targetGroup.getGroupImageUrl();

            // 내 게시물 댓글 작성자 정보
            this.userName = comment.getUser().getName();
            this.userProfileImageUrl = comment.getUser().getProfileImageUrl();

            this.contentId = content.getId();
            this.commentId = comment.getId();
            this.commentNote = comment.getCommentNote();
            this.stickerId = comment.getSticker() != null ? comment.getSticker().getId() : null;

            this.createdAt = notification.getCreatedAt();
            this.readYn = notification.isReadYn();
        }

        // 내 게시물 공감 response
        @Builder
        public NotificationInfo(Notification notification, Content content, Emotion emotion) {
            this.notificationId = notification.getId();
            this.notificationType = notification.getNotificationType();

            Group invitedGroup = notification.getContent().getGroup();
            this.groupId = invitedGroup.getId();
            this.groupName = invitedGroup.getGroupName();
            this.groupNote = invitedGroup.getGroupNote();
            this.groupImageUrl = invitedGroup.getGroupImageUrl();

            this.userName = emotion.getUser().getName();
            this.userProfileImageUrl = emotion.getUser().getProfileImageUrl();

            this.contentId = content.getId();

            this.emotionId = emotion.getId();
            this.emotionStatus = emotion.getEmotionStatus();

            this.createdAt = notification.getCreatedAt();
            this.readYn = notification.isReadYn();
        }

        // 그룹 새 멤버 알림 response
        @Builder
        public NotificationInfo(Group group, User newGroupUser, Notification notification) {
            this.notificationId = notification.getId();
            this.notificationType = notification.getNotificationType();

            // 새 구성원이 들어온 그룹 정보
            this.groupId = group.getId();
            this.groupName = group.getGroupName();
            this.groupNote = group.getGroupNote();
            this.groupImageUrl = group.getGroupImageUrl();

            // 그룹에 새로 가입한 구성원 정보
            this.userName = newGroupUser.getName();
            this.userProfileImageUrl = newGroupUser.getProfileImageUrl();

            this.createdAt = notification.getCreatedAt();
            this.readYn = notification.isReadYn();
        }
    }

    private long totalCount;
}

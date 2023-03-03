package dnd.diary.domain.group;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 일대일 양방향
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private Invite invite;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_like_id")
    private CommentLike commentLike;

    // 어떤 사용자의 알림인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 알림을 읽었는지 여부
    private boolean readYn;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    // 현재 상태에서는 '초대'만 알림 처리하여 유저가 받은 알림을 초대 목록을 통해 조회할 수 있지만
    // 이후 알림 타입이 추가될 경우 [유저 : 알림 = 1 : 다] 직접 조회 가능해야 함

    // 초대 알림
    @Builder
    private Notification(Invite invite, User user, NotificationType notificationType) {
        this.invite = invite;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toInviteEntity(Invite invite, User user, NotificationType notificationType) {
        return new Notification(invite, user, notificationType);
    }

    // 게시물 댓글 알림
    @Builder
    private Notification(Content content, Comment comment, User user, NotificationType notificationType) {
        this.content = content;
        this.comment = comment;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toContentCommentEntity(Content content, Comment comment, User user, NotificationType notificationType) {
        return new Notification(content, comment, user, notificationType);
    }

    // 게시물 공감 알림
    @Builder
    private Notification(Content content, Emotion emotion, User user, NotificationType notificationType) {
        this.content = content;
        this.emotion = emotion;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toContentEmotionEntity(Content content, Emotion emotion, User user, NotificationType notificationType) {
        return new Notification(content, emotion, user, notificationType);
    }

    // 댓글 공감 알림
    @Builder
    private Notification(Comment comment, Emotion emotion, User user, NotificationType notificationType) {
        this.comment = comment;
        this.emotion = emotion;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toCommentEmotionEntity(Comment comment, Emotion emotion, User user, NotificationType notificationType) {
        return new Notification(comment, emotion, user, notificationType);
    }

    // 알림 읽음 림리
    public void readNotification() {
        this.readYn = true;
    }

}

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
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_group_user_id")   // 중복 매핑으로 인해 외래키 별도 설정
    private User newGroupUser;

    // 어떤 사용자의 알림인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 알림을 읽었는지 여부
    private boolean readYn;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

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

    // 댓글 좋아요 알림
    @Builder
    private Notification(Comment comment, CommentLike commentLike, User user, NotificationType notificationType) {
        this.comment = comment;
        this.commentLike = commentLike;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toCommentLikeEntity(Comment comment, CommentLike commentLike, User user, NotificationType notificationType) {
        return new Notification(comment, commentLike, user, notificationType);
    }

    // 그룹 새 멤버 알림
    // group : 새로운 멤버가 가입한 그룹, user : 알림을 받을 그룹 구성원
    // group 의 새 멤버가 누구인지 정보 -> userName, userProfileImageUrl
    @Builder
    private Notification(Group group, User newGroupUser, User user, NotificationType notificationType) {
        this.group = group;
        this.newGroupUser = newGroupUser;

        this.user = user;
        this.readYn = false;
        this.notificationType = notificationType;

        user.getNotifications().add(this);
    }

    public static Notification toNewGroupMemberEntity(Group group, User newGroupUser, User user, NotificationType notificationType) {
        return new Notification(group, newGroupUser, user, notificationType);
    }

    // 알림 읽음 처리
    public void readNotification() {
        this.readYn = true;
    }

//    // emotion 삭제 시 공감 알림은 유지
//    public void remainEmotionNotification(){
//        Boolean emotionYn = this.emotion.getEmotionYn();
//        log.info("emotion 삭제 전 emotionYn 상태 : {}", emotionYn);
//        emotionYn = false;
//        log.info("emotion 삭제 후 emotionYn 상태 : {}", emotionYn);
//    }

    // emotion 변경시 알림 emotionId 변경
    public void updateEmotionNotification(){
        this.emotion = null;
    }

}

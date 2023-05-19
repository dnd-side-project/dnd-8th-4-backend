package dnd.diary.domain.user;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.sticker.UserStickerGroup;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.document.Document;
import dnd.diary.domain.document.DocumentFolder;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.Invite;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.mission.UserAssignMission;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "users")
@Builder
@DynamicUpdate
@Where(clause = "delete_at IS NULL")
@SQLDelete(sql = "UPDATE users SET delete_at = CURRENT_TIMESTAMP where user_id = ?")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String nickName;

    private String phoneNumber;

    private String profileImageUrl;

    private Long mainLevel;   // 스티커 획득 기준인 레벨
    private Double subLevel;   // 레벨 상승 기준 현재 위치(0 -> 1 -> 2 -> 3이 되면 레벨 + 1 / subLevel 0 으로)
    private LocalDateTime deleteAt;

    private boolean isNewNotification = Boolean.FALSE;   // 새로운 알림 생성 여부

    // 유저 권한
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    // 사용자가 가입한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserJoinGroup> userJoinGroups;

    // 사용자가 즐겨찾기 한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GroupStar> groupStars;

    // 사용자가 초대된 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Invite> invites;

    // 사용자가 작성한 게시물
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Content> contents;

    // 사용자가 남긴 게시물 공감
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Emotion> emotions;

    // 사용자가 북마크한 게시물 정보
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks;

    // 사용자의 모아보기 페이지
    @OneToOne(mappedBy = "user")
    private Document document;

    // 사용자의 모아보기 폴더 목록
    @OneToMany(mappedBy = "user")
    private List<DocumentFolder> documentFolders;

    // 사용자가 작성한 댓글
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    // 사용자가 가진 댓글 스티커
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserStickerGroup> userStickerGroups;

    // 사용자가 남긴 댓글 좋아요
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes;

    // 사용자에게 할당된 미션 목록 -> 그룹 구성원 모두에게 미션 할당
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAssignMission> userAssignMissions;

    // 사용자 알림 목록 - 그룹 초대 + a
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    protected User() {}

    @Builder
    private User(Long id, String email, String password, String name, String nickName, String phoneNumber, String profileImageUrl, Long mainLevel, Double subLevel, LocalDateTime deleteAt, boolean isNewNotification, Set<Authority> authorities, List<UserJoinGroup> userJoinGroups, List<GroupStar> groupStars, List<Invite> invites, List<Content> contents, List<Emotion> emotions, List<Bookmark> bookmarks, Document document, List<DocumentFolder> documentFolders, List<Comment> comments, List<UserStickerGroup> userStickerGroups, List<CommentLike> commentLikes, List<UserAssignMission> userAssignMissions, List<Notification> notifications) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.mainLevel = mainLevel;
        this.subLevel = subLevel;
        this.deleteAt = deleteAt;
        this.isNewNotification = isNewNotification;
        this.authorities = authorities;
        this.document = document;
        this.userJoinGroups = new ArrayList<>();
        this.groupStars = new ArrayList<>();
        this.invites = new ArrayList<>();
        this.contents = new ArrayList<>();
        this.emotions = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
        this.documentFolders = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.userStickerGroups = new ArrayList<>();
        this.commentLikes = new ArrayList<>();
        this.userAssignMissions = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public void updateLevel() {
        this.mainLevel += 1;
        this.subLevel = 0.0;
    }

    public void updateSubLevel() {
        this.subLevel += 0.5;
    }

    public void updateUserProfile(String nickName, String profileImageUrl){
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNewNotification() {
        this.isNewNotification = true;
    }
    public void updateReadNewNotification() {
        this.isNewNotification = false;
    }
}
package dnd.diary.domain.user;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.bookmark.Bookmark;
import dnd.diary.domain.comment.Comment;
import dnd.diary.domain.comment.CommentLike;
import dnd.diary.domain.comment.UserSticker;
import dnd.diary.domain.content.Content;
import dnd.diary.domain.content.Emotion;
import dnd.diary.domain.document.Document;
import dnd.diary.domain.document.DocumentFolder;
import dnd.diary.domain.group.GroupStar;
import dnd.diary.domain.group.Invite;
import dnd.diary.domain.group.Notification;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.mission.UserAssignMission;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@Builder
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
    private Long subLevel;   // 레벨 상승 기준 현재 위치(0 -> 1 -> 2 -> 3이 되면 레벨 + 1 / subLevel 0 으로)

    // 유저 권한
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    // 사용자가 가입한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<UserJoinGroup> userJoinGroups = new ArrayList<>();

    // 사용자가 즐겨찾기 한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<GroupStar> groupStars = new ArrayList<>();

    // 사용자가 초대된 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Invite> invites = new ArrayList<>();

    // 사용자가 작성한 게시물
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Content> contents = new ArrayList<>();

    // 사용자가 남긴 게시물 공감
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Emotion> emotions = new ArrayList<>();

    // 사용자가 북마크한 게시물 정보
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Bookmark> bookmarks = new ArrayList<>();

    // 사용자의 모아보기 페이지
    @OneToOne(mappedBy = "user")
    private Document document;

    // 사용자의 모아보기 폴더 목록
    @OneToMany(mappedBy = "user")
    private final List<DocumentFolder> documentFolders = new ArrayList<>();

    // 사용자가 작성한 댓글
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();

    // 사용자가 가진 댓글 스티커
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<UserSticker> userStickers = new ArrayList<>();

    // 사용자가 남긴 댓글 좋아요
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<CommentLike> commentLikes = new ArrayList<>();

    // 사용자에게 할당된 미션 목록 -> 그룹 구성원 모두에게 미션 할당
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAssignMission> userAssignMissions = new ArrayList<>();

    // 사용자 알림 목록 - 그룹 초대 + a
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Notification> notifications = new ArrayList<>();

    public void updateLevel() {
        this.mainLevel += 1;
        this.subLevel = 0L;
    }

    public void updateSubLevel() {
        this.subLevel += 1;
    }
}

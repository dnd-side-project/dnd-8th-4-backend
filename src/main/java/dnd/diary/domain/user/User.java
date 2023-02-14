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
import dnd.diary.domain.mission.Mission;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
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

    // 사용자가 가입한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserJoinGroup> userJoinGroups = new ArrayList<>();

    // 사용자가 즐겨찾기 한 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GroupStar> groupStars = new ArrayList<>();

    // 사용자가 초대된 그룹 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Invite> invites = new ArrayList<>();

    // 사용자가 작성한 게시물
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();

    // 사용자가 남긴 게시물 공감
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Emotion> emotions = new ArrayList<>();

    // 사용자가 북마크한 게시물 정보
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // 사용자의 모아보기 페이지
    @OneToOne(mappedBy = "user")
    private Document document;

    // 사용자의 모아보기 폴더 목록
    @OneToMany(mappedBy = "user")
    private List<DocumentFolder> documentFolders = new ArrayList<>();

    // 사용자가 작성한 댓글
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // 사용자가 가진 댓글 스티커
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSticker> userStickers = new ArrayList<>();

    // 사용자가 남긴 댓글 좋아요
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes = new ArrayList<>();

    // 사용자가 등록한 미션 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Mission> missions = new ArrayList<>();

}

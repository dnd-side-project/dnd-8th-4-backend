package dnd.diary.domain.group;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.user.User;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.domain.content.Content;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static dnd.diary.domain.mission.DateUtil.convertLocalDateTimeZone;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "groups")
@SQLDelete(sql = "UPDATE groups SET deleted = true WHERE group_id = ?")
@Where(clause = "deleted = false")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @NotNull
    private String groupName;

    private String groupNote;

    private String groupImageUrl;

    // 그룹 생성자
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User groupCreateUser;

    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime recentUpdatedAt;   // 게시물 최신 등록일

    private boolean deleted = Boolean.FALSE;   // 그룹 삭제 여부

    private boolean deletedYn = Boolean.FALSE;  // 삭제 여부 컬럼

    // 그룹에 가입한 유저 정보
    @OneToMany(mappedBy = "group")
    private List<UserJoinGroup> userJoinGroups = new ArrayList<>();

    // 그룹 내 작성된 게시물
    @OneToMany(mappedBy = "group")
    private List<Content> contents = new ArrayList<>();

    // 그룹의 즐겨찾기 정보
    @OneToMany(mappedBy = "group")
    private List<GroupStar> groupStars = new ArrayList<>();

    // 그룹의 초대 정보
    @OneToMany(mappedBy = "group")
    private List<Invite> invites = new ArrayList<>();

    // 그룹 내 등록된 미션 목록
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Mission> missions = new ArrayList<>();

    @Builder
    private Group(String groupName, String groupNote, String groupImageUrl, User groupCreateUser) {
        this.groupName = groupName;
        this.groupNote = groupNote;
        this.groupImageUrl = groupImageUrl;
        this.groupCreateUser = groupCreateUser;
        this.recentUpdatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));   // 그룹 생성일을 초기값으로
        this.deleted = false;
        this.deletedYn = false;
    }

    public static Group toEntity(String groupName, String groupNote, String groupImageUrl, User groupCreateUser) {
        return new Group(groupName, groupNote, groupImageUrl, groupCreateUser);
    }

    public void update(String groupName, String groupNote, String groupImageUrl) {
        this.groupName = groupName;
        this.groupNote = groupNote;
        this.groupImageUrl = groupImageUrl;
    }

    public void updateRecentModifiedAt() {
        this.recentUpdatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @PreRemove
    public void deleteGroup() {
        this.deleted = false;
    }

    public void deleteGroupByColumn() {
        this.deletedYn = true;
    }
}
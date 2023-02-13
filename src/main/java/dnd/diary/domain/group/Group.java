package dnd.diary.domain.group;

import com.sun.istack.NotNull;
import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.mission.Mission;
import dnd.diary.domain.user.UserJoinGroup;
import dnd.diary.domain.content.Content;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "groups")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @NotNull
    private String groupName;

    private String groupNote;

    private String groupImageUrl;

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
}
package dnd.diary.domain.group;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GroupStar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_starts_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private GroupStarStatus groupStarStatus;

    @Builder
    private GroupStar(Group group, User user) {
        this.group = group;
        this.user = user;
        this.groupStarStatus = GroupStarStatus.ADD;
    }

    public static GroupStar toEntity(Group group, User user) {
        return new GroupStar(group, user);
    }

    public void update(GroupStarStatus groupStarStatus) {
        this.groupStarStatus = groupStarStatus;
    }
}

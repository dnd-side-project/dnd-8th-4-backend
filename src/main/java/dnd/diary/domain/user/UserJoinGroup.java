package dnd.diary.domain.user;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.group.Group;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserJoinGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_join_group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public UserJoinGroup(User user, Group group) {
        this.user = user;
        this.group = group;

        user.getUserJoinGroups().add(this);
    }

    public static UserJoinGroup toEntity(User user, Group group) {
        return new UserJoinGroup(user, group);
    }

}

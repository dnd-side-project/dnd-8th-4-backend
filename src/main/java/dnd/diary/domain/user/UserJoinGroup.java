package dnd.diary.domain.user;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.group.Group;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE user_join_group SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
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

    private boolean deleted = Boolean.FALSE;   // 그룹 탈퇴 여부

    @Builder
    public UserJoinGroup(User user, Group group) {
        this.user = user;
        this.group = group;
        this.deleted = false;   // default 값 지정

        user.getUserJoinGroups().add(this);
    }

    public static UserJoinGroup toEntity(User user, Group group) {
        return new UserJoinGroup(user, group);
    }

    @PreRemove
    public void deleteUserJoinGroup() {
        this.deleted = false;
    }
}

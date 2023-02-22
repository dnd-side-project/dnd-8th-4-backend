package dnd.diary.domain.group;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Invite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private Long id;

    // 어느 그룹의 초대인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    // 누가 초대 받았는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 초대에 대한 행동(수락/거절)을 했는지 여부 - 수락/거절 api 호출 후 true 로
    private boolean processYn;

    @Builder
    private Invite(Group group, User user) {
        this.group = group;
        this.user = user;
        this.processYn = false;
    }

    public static Invite toEntity(Group group, User user) {
        return new Invite(group, user);
    }

    // 초대에 대한 수락/거절 처리 상태 변경
    public void updateProcessUYn() {
        this.processYn = true;
    }
}

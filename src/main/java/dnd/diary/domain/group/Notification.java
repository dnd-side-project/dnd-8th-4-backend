package dnd.diary.domain.group;

import dnd.diary.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private Invite invite;

    // 알림을 읽었는지 여부
    private boolean readYn;

    @Builder
    private Notification(Invite invite) {
        this.invite = invite;
        this.readYn = false;

        invite.getNotifications().add(this);
    }

    public static Notification toEntity(Invite invite) {
        return new Notification(invite);
    }

    // 알림 읽음 처리
    public void readNotification() {
        this.readYn = true;
    }

}

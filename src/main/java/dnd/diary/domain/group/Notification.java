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
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 일대일 양방향
    @OneToOne(mappedBy = "notification")
    private Invite invite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;   // 어떤 사용자의 알림인지

    // 알림을 읽었는지 여부
    private boolean readYn;

    // 현재 상태에서는 '초대'만 알림 처리하여 유저가 받은 알림을 초대 목록을 통해 조회할 수 있지만
    // 이후 알림 타입이 추가될 경우 [유저 : 알림 = 1 : 다] 직접 조회 가능해야 함
    @Builder
    private Notification(Invite invite, User user) {
        this.invite = invite;
        this.user = user;
        this.readYn = false;

        user.getNotifications().add(this);
    }

    public static Notification toEntity(Invite invite, User user) {
        return new Notification(invite, user);
    }

    // 알림 읽음 처리
    public void readNotification() {
        this.readYn = true;
    }

}

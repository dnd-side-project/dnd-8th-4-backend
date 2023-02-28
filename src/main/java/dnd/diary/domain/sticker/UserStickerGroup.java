package dnd.diary.domain.sticker;

import dnd.diary.domain.BaseEntity;
import dnd.diary.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 사용자가 보유한 스티커 목록 - StickerGroup 으로 부터 Sticker 목록 조회
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserStickerGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sticker_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_group_id")
    private StickerGroup stickerGroup;

    private UserStickerGroup(User user, StickerGroup stickerGroup) {
        this.user = user;
        this.stickerGroup = stickerGroup;
    }

    public static UserStickerGroup toEntity(User user, StickerGroup stickerGroup) {
        return new UserStickerGroup(user, stickerGroup);
    }
}

package dnd.diary.domain.sticker;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StickerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_group_id")
    private Long id;

    @NotNull
    private String stickerGroupName;

    @NotNull
    private Long stickerGroupLevel;

    @NotNull
    private String stickerGroupThumbnailUrl;

    @OneToMany(mappedBy = "stickerGroup")
    private List<UserStickerGroup> userStickerGroups = new ArrayList<>();

    @OneToMany(mappedBy = "stickerGroup")
    private List<Sticker> stickers = new ArrayList<>();

    private StickerGroup(String stickerGroupName, Long stickerGroupLevel, String stickerGroupThumbnailUrl) {
        this.stickerGroupName = stickerGroupName;
        this.stickerGroupLevel = stickerGroupLevel;
        this.stickerGroupThumbnailUrl = stickerGroupThumbnailUrl;
    }

    public static StickerGroup toEntity(String stickerGroupName, Long stickerGroupLevel, String stickerGroupThumbnailUrl) {
        return new StickerGroup(stickerGroupName, stickerGroupLevel, stickerGroupThumbnailUrl);
    }
}
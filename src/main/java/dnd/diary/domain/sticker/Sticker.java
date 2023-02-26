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
public class Sticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_id")
    private Long id;

    @NotNull
    private String stickerName;

    @NotNull
    private Long stickerLevel;

    @NotNull
    private String stickerUrl;

    @OneToMany(mappedBy = "sticker")
    private List<UserSticker> userStickers = new ArrayList<>();

    private Sticker(String stickerName, Long stickerLevel, String stickerUrl) {
        this.stickerName = stickerName;
        this.stickerLevel = stickerLevel;
        this.stickerUrl = stickerUrl;
    }

    public static Sticker toEntity(String stickerName, Long stickerLevel, String stickerUrl) {
        return new Sticker(stickerName, stickerLevel, stickerUrl);
    }
}
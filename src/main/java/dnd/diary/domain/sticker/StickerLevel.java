package dnd.diary.domain.sticker;

import lombok.Getter;

@Getter
public enum StickerLevel {

    LEVEL_TWO(2, "위어리의 알유 위어리?"),
    LEVEL_FIVE(5, "위어리의 내가 짱임!"),
    LEVEL_10(10, "바쁘다 바빠 위어리!");

    private final int code;
    private final String desc;

    StickerLevel(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }
}

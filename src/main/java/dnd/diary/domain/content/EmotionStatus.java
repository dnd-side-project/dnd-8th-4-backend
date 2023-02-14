package dnd.diary.domain.content;

import lombok.Getter;

@Getter
public enum EmotionStatus {
    LOVE(0, "LOVE"),
    LIKE(1, "LIKE"),
    NICE(2, "NICE"),
    AMAZING(3, "AMAZING"),
    SAD(4, "SAD"),
    ANGRY(5, "ANGRY");

    private final int code;
    private final String desc;

    EmotionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

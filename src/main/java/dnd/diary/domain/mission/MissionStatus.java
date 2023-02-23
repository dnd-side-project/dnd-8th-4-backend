package dnd.diary.domain.mission;

import lombok.Getter;

@Getter
public enum MissionStatus {
    ALL(0, "전체"),
    READY(1, "시작 전"),
    ACTIVE(2, "진행 중"),
    FINISH(3, "종료");

    private final int code;
    private final String desc;

    MissionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getName(int code) {
        for (MissionStatus value : MissionStatus.values()) {
            if (value.getCode() == code) {
                return value.name();
            }
        }
        return ALL.name();
    }
}

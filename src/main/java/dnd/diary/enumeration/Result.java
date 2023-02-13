package dnd.diary.enumeration;

import lombok.Getter;

@Getter
public enum Result {

    OK(0, "성공"),
    FAIL(-1, "실패");

    private final int code;
    private final String message;

    Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result resolve(int code) {
        for (Result result : values()) {
            if (result.getCode() == code) {
                return result;
            }
        }
        return null;
    }
}
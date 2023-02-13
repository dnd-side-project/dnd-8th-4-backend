package dnd.diary.exception;

import dnd.diary.enumeration.Result;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private Result result;
    private String debug;

    public CustomException(Result result) {
        this.result = result;
        this.debug = result.getMessage();
    }
}

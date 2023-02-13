package dnd.diary.response;

import dnd.diary.enumeration.Result;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomResponseEntity<T> {

    private int code;
    private String message;
    private T data;

    public static <T> CustomResponseEntity<T> success(T data) {
        return CustomResponseEntity.<T>builder()
                .code(Result.OK.getCode())
                .message(Result.OK.getMessage())
                .data(data)
                .build();
    }

    public static <T> CustomResponseEntity<T> fail(Result result) {
        return CustomResponseEntity.<T>builder()
                .code(result.getCode())
                .message(result.getMessage())
                .build();
    }

    @Builder
    public CustomResponseEntity(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}

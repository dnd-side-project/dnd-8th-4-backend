package dnd.diary.exception;

import dnd.diary.enumeration.Result;
import dnd.diary.response.CustomResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CommonRestExceptionHandler extends RuntimeException {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public CustomResponseEntity<String> handleExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("defaultExceptionHandler", e);
        return CustomResponseEntity.fail(Result.FAIL);
    }

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public CustomResponseEntity<String> handleCustomExceptionHandler(CustomException exception) {
        log.error("CustomExceptionHandler code : {}, message : {}",
                exception.getResult().getCode(), exception.getResult().getMessage());
        return CustomResponseEntity.fail(exception.getResult());
    }

	@ResponseBody
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return CustomResponseEntity.builder()
				.code(-1)
				.message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
				.build();
    }

	@ResponseBody
    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MissingServletRequestParameterException e, HttpServletRequest request
    ) {
        log.error("url {}, message: {}",
                request.getRequestURI(), e.getParameterName() + " 값이 등록되지 않았습니다.");
		return CustomResponseEntity.builder()
				.code(-1)
				.message(e.getParameterName() + " 값이 등록되지 않았습니다.")
				.build();
    }
}
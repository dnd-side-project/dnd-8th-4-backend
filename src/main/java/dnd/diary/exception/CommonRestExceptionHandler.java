package dnd.diary.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dnd.diary.enumeration.Result;
import dnd.diary.response.CustomResponseEntity;
import lombok.extern.slf4j.Slf4j;

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
}
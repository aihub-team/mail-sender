package kr.or.aihub.mailsender.global.utils.controller;

import kr.or.aihub.mailsender.domain.user.error.NotMatchPasswordException;
import kr.or.aihub.mailsender.domain.user.error.UserNotFoundException;
import kr.or.aihub.mailsender.global.utils.controller.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 컨트롤러 전체 에러 핸들링을 담당합니다.
 */
@ControllerAdvice
public class GlobalControllerErrorAdvice {

    /**
     * 유저를 찾지 못했을 경우를 처리합니다.
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userNotFoundExceptionHandler(UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 비밀번호가 일치하지 않을 경우를 처리합니다.
     */
    @ExceptionHandler(NotMatchPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notMatchPasswordExceptionHandler(NotMatchPasswordException e) {
        return new ErrorResponse(e.getMessage());
    }
}
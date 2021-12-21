package kr.or.aihub.mailsender.controller;

import kr.or.aihub.mailsender.errors.InvalidAccessTokenException;
import kr.or.aihub.mailsender.errors.NoExistAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidAccessTokenException.class)
    public void invalidAccessTokenExceptionHandler() {
        doNothing();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoExistAccessTokenException.class)
    public void noExistAccessTokenExceptionHandler() {
        doNothing();
    }

    /**
     * 아무 작업도 하지 않는 것을 강조하기 위해 만든 메서드 입니다.
     */
    private void doNothing() {
    }
}

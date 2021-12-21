package kr.or.aihub.mailsender.controller;

import kr.or.aihub.mailsender.errors.InvalidAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidAccessTokenException.class)
    public void invalidAccessTokenExceptionHandler() {
        // HTTP STATUS CODE 반환 후 아무것도 하지 않습니다.
    }
}

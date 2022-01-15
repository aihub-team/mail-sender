package kr.or.aihub.mailsender.global.utils.controller;

import kr.or.aihub.mailsender.domain.user.error.ConfirmPasswordNotMatchException;
import kr.or.aihub.mailsender.domain.user.error.DeactivateUserException;
import kr.or.aihub.mailsender.domain.user.error.ExistUsernameException;
import kr.or.aihub.mailsender.domain.user.error.PasswordNotMatchException;
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
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse passwordNotMatchExceptionHandler(PasswordNotMatchException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 존재하는 유저이름이 회원가입을 시도할 경우를 처리합니다.
     */
    @ExceptionHandler(ExistUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse existUsernameExceptionHandler(ExistUsernameException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 비밀번호 확인이 일치하지 않을 경우를 처리합니다.
     */
    @ExceptionHandler(ConfirmPasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse confirmPasswordNotMatchExceptionHandler(ConfirmPasswordNotMatchException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 활성화되지 않은 유저일 경우를 처리합니다.
     */
    @ExceptionHandler(DeactivateUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse deactivateUserExceptionHandler(DeactivateUserException e) {
        return new ErrorResponse(e.getMessage());
    }
}

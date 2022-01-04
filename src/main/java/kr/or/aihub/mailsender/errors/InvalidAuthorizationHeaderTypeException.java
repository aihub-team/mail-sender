package kr.or.aihub.mailsender.errors;

public class InvalidAuthorizationHeaderTypeException extends RuntimeException {
    public InvalidAuthorizationHeaderTypeException() {
        super("올바르지 않은 Authorization 헤더 시작 문자입니다.");
    }
}

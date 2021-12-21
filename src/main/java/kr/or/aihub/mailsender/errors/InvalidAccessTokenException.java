package kr.or.aihub.mailsender.errors;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException() {
        super("잘못된 액세스 토큰입니다.");
    }
}

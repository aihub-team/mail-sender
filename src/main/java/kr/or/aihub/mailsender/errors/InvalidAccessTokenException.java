package kr.or.aihub.mailsender.errors;

/**
 * 토큰이 주어졌으나 잘못되었을 때 던집니다.
 */
public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException() {
        super("잘못된 액세스 토큰입니다.");
    }
}

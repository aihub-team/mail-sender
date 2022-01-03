package kr.or.aihub.mailsender.errors;

/**
 * 액세스 토큰이 비어있을 때 던집니다.
 */
public class EmptyAccessTokenException extends RuntimeException {
    public EmptyAccessTokenException() {
        super("빈 액세스 토큰입니다.");
    }
}

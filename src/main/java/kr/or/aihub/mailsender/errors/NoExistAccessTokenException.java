package kr.or.aihub.mailsender.errors;

/**
 * 액세스 토큰이 존재하지 않을 때 던집니다.
 */
public class NoExistAccessTokenException extends RuntimeException {
    public NoExistAccessTokenException() {
        super("액세스 토큰이 존재하지 않습니다. 인증 처리를 진행해주세요");
    }
}

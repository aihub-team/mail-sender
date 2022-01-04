package kr.or.aihub.mailsender.errors;

/**
 * Jwt Credentials가 비어있을 때 던집니다.
 */
public class EmptyJwtCredentialsException extends RuntimeException {
    public EmptyJwtCredentialsException() {
        super("토큰이 비어있습니다. 인증 처리를 다시 진행해주세요.");
    }
}

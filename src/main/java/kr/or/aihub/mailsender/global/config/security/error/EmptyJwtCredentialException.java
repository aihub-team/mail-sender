package kr.or.aihub.mailsender.global.config.security.error;

/**
 * Jwt Credential이 비어있을 때 던집니다.
 */
public class EmptyJwtCredentialException extends RuntimeException {
    public EmptyJwtCredentialException() {
        super("토큰이 비어있습니다. 인증 처리를 다시 진행해주세요.");
    }
}

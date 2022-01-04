package kr.or.aihub.mailsender.errors;

/**
 * 허용되지 않은 Jwt 타입일 경우 던집니다.
 */
public class NotAllowedJwtTypeException extends RuntimeException {
    public NotAllowedJwtTypeException() {
        super("허용되지 않은 Jwt 타입입니다.");
    }
}

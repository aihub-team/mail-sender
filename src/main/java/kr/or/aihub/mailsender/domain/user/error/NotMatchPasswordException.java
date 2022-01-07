package kr.or.aihub.mailsender.domain.user.error;

/**
 * 패스워드가 일치하지 않을 경우 던집니다.
 */
public class NotMatchPasswordException extends RuntimeException {
    public NotMatchPasswordException(String username) {
        super(String.format("일치하지 않는 패스워드 입니다. 유저: %s", username));
    }
}

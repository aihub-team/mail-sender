package kr.or.aihub.mailsender.domain.user.error;

/**
 * 비밀번호가 일치하지 않을 경우 던집니다.
 */
public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String username) {
        super(String.format("비밀번호가 일치하지 않습니다. 유저: %s", username));
    }
}

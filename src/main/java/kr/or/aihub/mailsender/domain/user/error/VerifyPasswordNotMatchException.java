package kr.or.aihub.mailsender.domain.user.error;

/**
 * 비밀번호 확인이 일치하지 않을경우 던집니다.
 */
public class VerifyPasswordNotMatchException extends RuntimeException {
    public VerifyPasswordNotMatchException() {
        super("비밀번호 확인이 일치하지 않습니다.");
    }
}

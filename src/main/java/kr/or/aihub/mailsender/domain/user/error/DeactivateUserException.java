package kr.or.aihub.mailsender.domain.user.error;

/**
 * 활성화되지 않은 유저일 경우 던집니다.
 */
public class DeactivateUserException extends RuntimeException {
    public DeactivateUserException(String username) {
        super(String.format("활성화되지 않은 유저입니다. 관리자에게 문의하세요: %s", username));
    }
}

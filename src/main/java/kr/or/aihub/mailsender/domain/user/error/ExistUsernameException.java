package kr.or.aihub.mailsender.domain.user.error;

/**
 * 이미 존재하는 유저이름일 경우 던집니다.
 */
public class ExistUsernameException extends RuntimeException {

    public ExistUsernameException(String username) {
        super(String.format("존재하는 유저이름 입니다: %s", username));
    }
}

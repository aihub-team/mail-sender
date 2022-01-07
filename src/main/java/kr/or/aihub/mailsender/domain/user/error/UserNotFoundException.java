package kr.or.aihub.mailsender.domain.user.error;

/**
 * 유저를 찾지 못할 경우 던집니다.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("존재하지 않는 유저입니다: %s", username));
    }
}

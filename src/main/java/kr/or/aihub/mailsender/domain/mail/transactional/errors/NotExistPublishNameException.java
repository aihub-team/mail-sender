package kr.or.aihub.mailsender.domain.mail.transactional.errors;

public class NotExistPublishNameException extends RuntimeException {
    public NotExistPublishNameException(String publishName) {
        super(String.format("존재하지 않는 발행 이름입니다: %s", publishName));
    }
}

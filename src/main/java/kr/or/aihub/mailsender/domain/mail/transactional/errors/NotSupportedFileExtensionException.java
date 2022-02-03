package kr.or.aihub.mailsender.domain.mail.transactional.errors;

public class NotSupportedFileExtensionException extends RuntimeException {

    public NotSupportedFileExtensionException(String extension) {
        super(String.format("지원되지 않는 확장자입니다" + ": %s", extension));
    }
}

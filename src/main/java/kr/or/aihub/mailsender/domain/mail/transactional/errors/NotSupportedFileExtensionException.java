package kr.or.aihub.mailsender.domain.mail.transactional.errors;

public class NotSupportedFileExtensionException extends RuntimeException {
    private static final String PREFIX_MESSAGE = "지원되지 않는 확장자입니다";

    public NotSupportedFileExtensionException(String extension) {
        super(String.format(PREFIX_MESSAGE + ": %s", extension));
    }
}

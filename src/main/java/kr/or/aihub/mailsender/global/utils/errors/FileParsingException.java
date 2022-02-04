package kr.or.aihub.mailsender.global.utils.errors;

public class FileParsingException extends RuntimeException {

    public FileParsingException() {
        super("파일 파싱 예외 발생");
    }
}

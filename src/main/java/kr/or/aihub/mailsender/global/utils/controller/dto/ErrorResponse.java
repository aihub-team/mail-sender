package kr.or.aihub.mailsender.global.utils.controller.dto;

/**
 * 에러 응답 객체.
 */
public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}

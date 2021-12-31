package kr.or.aihub.mailsender.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionCreateResponseData {
    private String accessToken;

    @Builder
    public SessionCreateResponseData(String accessToken) {
        this.accessToken = accessToken;
    }
}

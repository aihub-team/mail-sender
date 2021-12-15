package kr.or.aihub.mailsender.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSessionResponseData {
    private String accessToken;

    @Builder
    public PostSessionResponseData(String accessToken) {
        this.accessToken = accessToken;
    }
}

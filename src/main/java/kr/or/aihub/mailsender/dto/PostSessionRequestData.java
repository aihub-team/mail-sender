package kr.or.aihub.mailsender.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSessionRequestData {
    private String username;
    private String password;

    @Builder
    public PostSessionRequestData(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

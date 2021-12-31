package kr.or.aihub.mailsender.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionCreateRequestData {
    private String username;
    private String password;

    @Builder
    public SessionCreateRequestData(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

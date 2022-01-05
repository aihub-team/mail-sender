package kr.or.aihub.mailsender.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponse {
    private final String accessToken;

    @Builder
    public UserLoginResponse(String jwtCredential) {
        this.accessToken = jwtCredential;
    }
}

package kr.or.aihub.mailsender.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionCreateResponseData {
    private String jwtCredential;

    @Builder
    public SessionCreateResponseData(String jwtCredential) {
        this.jwtCredential = jwtCredential;
    }
}

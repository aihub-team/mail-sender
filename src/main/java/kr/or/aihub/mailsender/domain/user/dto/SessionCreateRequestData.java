package kr.or.aihub.mailsender.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class SessionCreateRequestData {

    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotBlank
    @Size(min = 4, max = 20)
    private String password;

    public SessionCreateRequestData() {
    }

    @Builder
    public SessionCreateRequestData(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

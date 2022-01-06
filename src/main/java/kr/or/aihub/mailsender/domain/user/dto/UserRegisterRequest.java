package kr.or.aihub.mailsender.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserRegisterRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    private final String username;

    @NotBlank
    @Size(min = 4, max = 20)
    private final String password;

    @NotBlank
    @Size(min = 4, max = 20)
    private final String verifyPassword;

    @Builder
    public UserRegisterRequest(String username, String password, String verifyPassword) {
        this.username = username;
        this.password = password;
        this.verifyPassword = verifyPassword;
    }
}

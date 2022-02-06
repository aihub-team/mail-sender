package kr.or.aihub.mailsender.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
public class UserRegisterRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotBlank
    @Size(min = 4, max = 20)
    private String password;

    @NotBlank
    @Size(min = 4, max = 20)
    private String confirmPassword;

    protected UserRegisterRequest() {
    }

    @Builder
    public UserRegisterRequest(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    /**
     * 패스워드와 패스워드 확인이 일치하면 true, 일치하지 않으면 false를 리턴합니다.
     *
     * @return 패스워드와 패스워드 확인이 일치하면 true, 일치하지 않으면 false
     */
    public boolean matchPassword() {
        return Objects.equals(password, confirmPassword);
    }
}

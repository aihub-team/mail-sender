package kr.or.aihub.mailsender.domain.user.controller;

import kr.or.aihub.mailsender.domain.user.application.UserLoginService;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 유저 관련 HTTP 요청 처리 담당.
 */
@RestController
@RequestMapping(path = "user")
public class UserController {
    private UserLoginService userLoginService;

    public UserController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    /**
     * 유저 로그인 요청에 필요한 데이터를 받아, 새로 발급한 액세스 토큰을 리턴합니다.
     *
     * @param userLoginRequest 유저 로그인 요청 시 필요한 데이터
     * @return 새로 발급한 액세스 토큰
     */
    @PostMapping("login")
    @ResponseStatus(HttpStatus.CREATED)
    public UserLoginResponse postSession(
            @RequestBody @Valid UserLoginRequest userLoginRequest
    ) {
        String username = userLoginRequest.getUsername();

        String jwtCredential = userLoginService.login(username);

        return UserLoginResponse.builder()
                .jwtCredential(jwtCredential)
                .build();
    }
}

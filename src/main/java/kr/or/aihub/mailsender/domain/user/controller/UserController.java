package kr.or.aihub.mailsender.domain.user.controller;

import kr.or.aihub.mailsender.domain.user.application.UserLoginService;
import kr.or.aihub.mailsender.domain.user.application.UserRegisterService;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginRequest;
import kr.or.aihub.mailsender.domain.user.dto.UserLoginResponse;
import kr.or.aihub.mailsender.domain.user.dto.UserRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 유저 관련 HTTP 요청 처리 담당.
 */
@RestController
@RequestMapping(path = "user")
public class UserController {
    private final UserLoginService userLoginService;
    private final UserRegisterService userRegisterService;

    public UserController(UserLoginService userLoginService, UserRegisterService userRegisterService) {
        this.userLoginService = userLoginService;
        this.userRegisterService = userRegisterService;
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
        String jwtCredential = userLoginService.login(userLoginRequest);

        return UserLoginResponse.builder()
                .accessToken(jwtCredential)
                .build();
    }

    /**
     * 회원가입에 필요한 데이터를 받아 회원가입 요청을 수행 후,
     * <p>
     * 로그인 페이지로 리다이렉트 합니다.
     *
     * @param userRegisterRequest 회원가입 시 필요한 데이터
     */
    @PostMapping("register")
    @ResponseStatus(HttpStatus.FOUND)
    public void register(
            @RequestBody @Valid UserRegisterRequest userRegisterRequest,
            HttpServletResponse httpServletResponse
    ) throws IOException {
        userRegisterService.registerUser(userRegisterRequest);

        httpServletResponse.sendRedirect("/user/login");
    }

    // TODO: 2022/01/06 로그인 페이지 구현 필요
    @GetMapping("login")
    public String login() {
        return "로그인 페이지 개발중...";
    }
}

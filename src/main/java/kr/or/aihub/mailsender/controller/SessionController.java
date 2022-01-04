package kr.or.aihub.mailsender.controller;

import kr.or.aihub.mailsender.dto.SessionCreateRequestData;
import kr.or.aihub.mailsender.dto.SessionCreateResponseData;
import kr.or.aihub.mailsender.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 세션 관련 HTTP 요청 처리 담당.
 */
@RestController
@RequestMapping(path = "session")
public class SessionController {
    private LoginService loginService;

    public SessionController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 세션 생성에 필요한 정보를 받아, 새로 발급한 액세스 토큰을 리턴합니다.
     *
     * @param sessionCreateRequestData 세션 생성시 필요 데이터
     * @return 새로 발급한 액세스 토큰
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionCreateResponseData postSession(
            @RequestBody @Valid SessionCreateRequestData sessionCreateRequestData
    ) {
        String username = sessionCreateRequestData.getUsername();

        String jwtCredential = loginService.login(username);

        return SessionCreateResponseData.builder()
                .jwtCredential(jwtCredential)
                .build();
    }
}

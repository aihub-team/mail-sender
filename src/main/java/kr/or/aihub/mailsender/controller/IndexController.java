package kr.or.aihub.mailsender.controller;

import kr.or.aihub.mailsender.service.AccessTokenAuthenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class IndexController {
    private final AccessTokenAuthenticator accessTokenAuthenticator;

    public IndexController(AccessTokenAuthenticator accessTokenAuthenticator) {
        this.accessTokenAuthenticator = accessTokenAuthenticator;
    }

    @GetMapping
    public ResponseEntity<String> index(
            @RequestHeader("Authorization") String authorization
    ) {
        String accessToken = authorization.substring("Bearer ".length());

        boolean accessTokenInvalid = accessTokenAuthenticator.authenticate(accessToken);
        if (!accessTokenInvalid) {
            return new ResponseEntity<>("올바르지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

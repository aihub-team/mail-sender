package kr.or.aihub.mailsender.controller;

import kr.or.aihub.mailsender.errors.InvalidAccessTokenException;
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

        boolean accessTokenValid = accessTokenAuthenticator.authenticate(accessToken);
        if (!accessTokenValid) {
            throw new InvalidAccessTokenException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

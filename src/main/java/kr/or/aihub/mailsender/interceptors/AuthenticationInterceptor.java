package kr.or.aihub.mailsender.interceptors;

import kr.or.aihub.mailsender.errors.InvalidAccessTokenException;
import kr.or.aihub.mailsender.errors.NoExistAccessTokenException;
import kr.or.aihub.mailsender.service.AccessTokenAuthenticator;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final AccessTokenAuthenticator accessTokenAuthenticator;

    public AuthenticationInterceptor(AccessTokenAuthenticator accessTokenAuthenticator) {
        this.accessTokenAuthenticator = accessTokenAuthenticator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = getAuthorizationHeader(request)
                .orElseThrow(NoExistAccessTokenException::new);
        String accessToken = authorizationHeader.substring("Bearer ".length());

        boolean accessTokenValid = accessTokenAuthenticator.authenticate(accessToken);
        if (!accessTokenValid) {
            throw new InvalidAccessTokenException();
        }

        return true;
    }

    private Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        return Optional.ofNullable(authorizationHeader);
    }
}

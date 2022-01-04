package kr.or.aihub.mailsender.filters;

import kr.or.aihub.mailsender.auth.UserAuthentication;
import kr.or.aihub.mailsender.errors.EmptyAccessTokenException;
import kr.or.aihub.mailsender.errors.InvalidAuthorizationHeaderTypeException;
import kr.or.aihub.mailsender.service.AccessTokenAuthenticator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * JWT 인증을 담당하는 필터.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AccessTokenAuthenticator accessTokenAuthenticator;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AccessTokenAuthenticator accessTokenAuthenticator) {
        super(authenticationManager);
        this.accessTokenAuthenticator = accessTokenAuthenticator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHttpRequestHeader = getAuthorizationRequestHeader(request)
                .orElseThrow(EmptyAccessTokenException::new);
        String accessToken = getAccessToken(authorizationHttpRequestHeader);

        accessTokenAuthenticator.authenticate(accessToken);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UserAuthentication());

        chain.doFilter(request, response);
    }

    private Optional<String> getAuthorizationRequestHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return Optional.ofNullable(authorization);
    }

    /**
     * Authorization 헤더를 통해 얻은 액세스 토큰을 리턴합니다.
     *
     * @param authorizationHeader Authorization 헤더 값
     * @return 액세스 토큰
     * @throws InvalidAuthorizationHeaderTypeException Authorization 헤더가 올바르지 않은 타입일 경우
     */
    private String getAccessToken(String authorizationHeader) {
        String validAuthorizationHeaderType = "Bearer ";
        if (!authorizationHeader.startsWith(validAuthorizationHeaderType)) {
            throw new InvalidAuthorizationHeaderTypeException();
        }

        return authorizationHeader.replace(validAuthorizationHeaderType, "");
    }
}

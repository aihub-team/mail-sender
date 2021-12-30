package kr.or.aihub.mailsender.filters;

import kr.or.aihub.mailsender.auth.UserAuthentication;
import kr.or.aihub.mailsender.errors.InvalidAccessTokenException;
import kr.or.aihub.mailsender.errors.NoExistAccessTokenException;
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
        String authorizationRequestHeader = getAuthorizationRequestHeader(request)
                .orElseThrow(NoExistAccessTokenException::new);
        String accessToken = getAccessToken(authorizationRequestHeader);

        boolean accessTokenValid = accessTokenAuthenticator.authenticate(accessToken);
        if (!accessTokenValid) {
            throw new InvalidAccessTokenException();
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UserAuthentication());

        chain.doFilter(request, response);
    }

    private Optional<String> getAuthorizationRequestHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return Optional.ofNullable(authorization);
    }

    private String getAccessToken(String authorizationRequestHeader) {
        return authorizationRequestHeader.substring("Bearer ".length());
    }
}

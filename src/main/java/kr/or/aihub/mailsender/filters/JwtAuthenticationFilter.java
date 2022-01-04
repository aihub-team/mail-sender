package kr.or.aihub.mailsender.filters;

import kr.or.aihub.mailsender.auth.UserAuthentication;
import kr.or.aihub.mailsender.errors.EmptyJwtCredentialException;
import kr.or.aihub.mailsender.errors.NotAllowedJwtTypeException;
import kr.or.aihub.mailsender.service.JwtCredentialAuthenticator;
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
    private final JwtCredentialAuthenticator jwtCredentialAuthenticator;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtCredentialAuthenticator jwtCredentialAuthenticator) {
        super(authenticationManager);
        this.jwtCredentialAuthenticator = jwtCredentialAuthenticator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHttpRequestHeader = getAuthorizationRequestHeader(request)
                .orElseThrow(EmptyJwtCredentialException::new);
        String jwtCredentials = getJwtCredentials(authorizationHttpRequestHeader);

        jwtCredentialAuthenticator.authenticate(jwtCredentials);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UserAuthentication());

        chain.doFilter(request, response);
    }

    private Optional<String> getAuthorizationRequestHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return Optional.ofNullable(authorization);
    }

    /**
     * Authorization 헤더를 통해 얻은 Jwt Credentials를 리턴합니다.
     *
     * @param authorizationHeader Authorization 헤더 값
     * @return Jwt Credentials
     * @throws NotAllowedJwtTypeException 허용되지 않은 Jwt 타입일 경우
     */
    private String getJwtCredentials(String authorizationHeader) {
        String allowedJwtType = "Bearer ";
        if (!authorizationHeader.startsWith(allowedJwtType)) {
            throw new NotAllowedJwtTypeException();
        }

        return authorizationHeader.replace(allowedJwtType, "");
    }
}

package kr.or.aihub.mailsender.global.config.security.filters;

import kr.or.aihub.mailsender.domain.role.application.RoleFinder;
import kr.or.aihub.mailsender.domain.role.domain.Role;
import kr.or.aihub.mailsender.global.config.security.auth.UserAuthentication;
import kr.or.aihub.mailsender.global.config.security.error.EmptyJwtCredentialException;
import kr.or.aihub.mailsender.global.config.security.error.NotAllowedJwtTypeException;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialDecoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * JWT 인증을 담당하는 필터.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtCredentialDecoder jwtCredentialDecoder;
    private final RoleFinder roleFinder;

    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtCredentialDecoder jwtCredentialDecoder,
            RoleFinder roleFinder
    ) {
        super(authenticationManager);
        this.jwtCredentialDecoder = jwtCredentialDecoder;
        this.roleFinder = roleFinder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHttpRequestHeader = getAuthorizationRequestHeader(request)
                .orElseThrow(EmptyJwtCredentialException::new);
        String jwtCredential = getJwtCredential(authorizationHttpRequestHeader);

        Long userId = (Long) jwtCredentialDecoder.decode(jwtCredential, "userId");
        List<Role> roles = roleFinder.findBy(userId);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UserAuthentication(userId, roles));

        chain.doFilter(request, response);
    }

    private Optional<String> getAuthorizationRequestHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        return Optional.ofNullable(authorization);
    }

    /**
     * Authorization 헤더를 통해 얻은 Jwt Credential를 리턴합니다.
     *
     * @param authorizationHeader Authorization 헤더 값
     * @return Jwt Credential
     * @throws NotAllowedJwtTypeException 허용되지 않은 Jwt 타입일 경우
     */
    private String getJwtCredential(String authorizationHeader) {
        String allowedJwtType = "Bearer ";
        if (!authorizationHeader.startsWith(allowedJwtType)) {
            throw new NotAllowedJwtTypeException();
        }

        return authorizationHeader.replace(allowedJwtType, "");
    }
}

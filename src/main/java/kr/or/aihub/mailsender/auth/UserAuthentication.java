package kr.or.aihub.mailsender.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 유저 인증 담당.
 */
public class UserAuthentication extends AbstractAuthenticationToken {

    public UserAuthentication() {
        super(authorities());

        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    private static List<GrantedAuthority> authorities() {
        return null;
    }
}

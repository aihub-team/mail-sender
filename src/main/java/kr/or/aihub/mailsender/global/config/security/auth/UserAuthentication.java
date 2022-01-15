package kr.or.aihub.mailsender.global.config.security.auth;

import kr.or.aihub.mailsender.domain.role.domain.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 유저 인증 담당.
 */
public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long userId;

    public UserAuthentication(Long userId, List<Role> roles) {
        super(authorities(roles));
        this.setAuthenticated(true);

        this.userId = userId;
    }

    private static List<GrantedAuthority> authorities(List<Role> roles) {
        return roles.stream()
                .map(role -> getSimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    private static SimpleGrantedAuthority getSimpleGrantedAuthority(Role role) {
        String roleType = role.getType().toString();

        return new SimpleGrantedAuthority(roleType);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }
}

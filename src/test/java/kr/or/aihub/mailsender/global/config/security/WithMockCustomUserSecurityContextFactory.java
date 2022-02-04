package kr.or.aihub.mailsender.global.config.security;

import kr.or.aihub.mailsender.global.config.security.auth.UserAuthentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        List<SimpleGrantedAuthority> authorities = Stream.of(annotation.roles())
                .map(it -> new SimpleGrantedAuthority(it.toString()))
                .collect(Collectors.toList());

        securityContext.setAuthentication(new UserAuthentication(authorities));

        return securityContext;
    }

}

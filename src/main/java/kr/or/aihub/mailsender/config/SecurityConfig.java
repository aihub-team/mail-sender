package kr.or.aihub.mailsender.config;

import kr.or.aihub.mailsender.filters.ExceptionHandleFilter;
import kr.or.aihub.mailsender.filters.JwtAuthenticationFilter;
import kr.or.aihub.mailsender.service.JwtCredentialAuthenticator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtCredentialAuthenticator jwtCredentialAuthenticator;

    public SecurityConfig(JwtCredentialAuthenticator jwtCredentialAuthenticator) {
        this.jwtCredentialAuthenticator = jwtCredentialAuthenticator;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Filter exceptionHandleFilter = new ExceptionHandleFilter();
        Filter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(),
                jwtCredentialAuthenticator);

        httpSecurity
                .antMatcher("/")
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(exceptionHandleFilter, JwtAuthenticationFilter.class)
                .httpBasic().disable()
                .cors().and()
                .csrf().disable();
    }
}

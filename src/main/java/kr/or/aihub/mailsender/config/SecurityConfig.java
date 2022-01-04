package kr.or.aihub.mailsender.config;

import kr.or.aihub.mailsender.filters.ErrorHandleFilter;
import kr.or.aihub.mailsender.filters.JwtAuthenticationFilter;
import kr.or.aihub.mailsender.service.JwtCredentialsAuthenticator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtCredentialsAuthenticator jwtCredentialsAuthenticator;

    public SecurityConfig(JwtCredentialsAuthenticator jwtCredentialsAuthenticator) {
        this.jwtCredentialsAuthenticator = jwtCredentialsAuthenticator;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Filter errorHandleFilter = new ErrorHandleFilter();
        Filter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(),
                jwtCredentialsAuthenticator);

        httpSecurity
                .antMatcher("/")
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(errorHandleFilter, JwtAuthenticationFilter.class)
                .httpBasic().disable()
                .cors().and()
                .csrf().disable();
    }
}

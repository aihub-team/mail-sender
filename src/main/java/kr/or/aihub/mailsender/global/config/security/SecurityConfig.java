package kr.or.aihub.mailsender.global.config.security;

import kr.or.aihub.mailsender.domain.role.application.RoleFinder;
import kr.or.aihub.mailsender.global.config.security.filters.ExceptionHandleFilter;
import kr.or.aihub.mailsender.global.config.security.filters.JwtAuthenticationFilter;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtCredentialDecoder jwtCredentialDecoder;
    private final Environment environment;
    private final RoleFinder roleFinder;

    public SecurityConfig(
            JwtCredentialDecoder jwtCredentialDecoder,
            Environment environment,
            RoleFinder roleFinder
    ) {
        this.jwtCredentialDecoder = jwtCredentialDecoder;
        this.environment = environment;
        this.roleFinder = roleFinder;
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/hello")
                .antMatchers("/user/*")
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        Filter exceptionHandleFilter = new ExceptionHandleFilter();
        Filter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(),
                jwtCredentialDecoder,
                roleFinder
        );

        httpSecurity
                .authorizeRequests()
                .antMatchers("/").hasRole("ACTIVATE")
                .antMatchers("/role/*").hasRole("ADMIN")
                .antMatchers("/mail/transactional/templates/*").hasRole("ACTIVATE");

        if (!isTestProfile()) {
            httpSecurity
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterBefore(exceptionHandleFilter, JwtAuthenticationFilter.class);
        }

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().disable()
                .cors().and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                );
    }

    private boolean isTestProfile() {
        String[] activeProfiles = environment.getActiveProfiles();

        if (activeProfiles.length == 0) {
            return false;
        }

        String lastActiveProfile = activeProfiles[activeProfiles.length - 1];

        return "test".equals(lastActiveProfile);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

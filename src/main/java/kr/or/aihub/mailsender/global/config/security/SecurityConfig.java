package kr.or.aihub.mailsender.global.config.security;

import kr.or.aihub.mailsender.domain.role.domain.RoleRepository;
import kr.or.aihub.mailsender.domain.user.domain.UserRepository;
import kr.or.aihub.mailsender.global.config.security.filters.ExceptionHandleFilter;
import kr.or.aihub.mailsender.global.config.security.filters.JwtAuthenticationFilter;
import kr.or.aihub.mailsender.global.utils.application.JwtCredentialDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public SecurityConfig(
            JwtCredentialDecoder jwtCredentialDecoder,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.jwtCredentialDecoder = jwtCredentialDecoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        Filter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(),
                jwtCredentialDecoder, userRepository, roleRepository);

        httpSecurity
                .authorizeRequests()
                .antMatchers("/").hasRole("ACTIVATE")
                .antMatchers("/role/*").hasRole("ADMIN")
                .antMatchers("/mail/transactional/templates/*").hasRole("ACTIVATE")
                .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(exceptionHandleFilter, JwtAuthenticationFilter.class)
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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

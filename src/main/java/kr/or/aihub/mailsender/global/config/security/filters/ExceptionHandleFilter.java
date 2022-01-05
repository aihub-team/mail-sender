package kr.or.aihub.mailsender.global.config.security.filters;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.global.config.security.error.EmptyJwtCredentialException;
import kr.or.aihub.mailsender.global.config.security.error.NotAllowedJwtTypeException;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필터의 예외 처리를 담당하는 필터.
 */
public class ExceptionHandleFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (EmptyJwtCredentialException | NotAllowedJwtTypeException exception) {
            response.sendError(
                    HttpStatus.BAD_REQUEST.value(),
                    exception.getMessage()
            );
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "인증에 실패하였습니다."
            );
        }
    }
}

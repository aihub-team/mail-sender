package kr.or.aihub.mailsender.filters;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import kr.or.aihub.mailsender.errors.EmptyAccessTokenException;
import kr.or.aihub.mailsender.errors.NoExistAccessTokenException;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필터의 에러 처리를 담당하는 필터.
 */
public class ErrorHandleFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (NoExistAccessTokenException e) {
            response.sendError(HttpStatus.BAD_REQUEST.value());
        } catch (EmptyAccessTokenException | SignatureException | MalformedJwtException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
}

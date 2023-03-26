package shop.mtcoding.bank.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 모든 주소에서 동작함 (토큰 검증)
 * - 토큰이 있을때만
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (isHeaderVerify(request, response)) {
            //토큰이 존재함
            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            LoginUser loginUser = JwtProcess.verify(token); //토큰 검증, 인증 (토큰에 id와 role만 들어감, username, password 모름)

            // 임시 세션
            // UsernamePasswordAuthenticationToken의 첫번째 인자에는 UserDetails타입 or username를 넣을 수있다.
            // 이 시점에서 username이 뭔지 별로 안중요함, token으로 인증을 이미 했고, role을 세션에 넣어 security의 권한처리 도움을 받기 위함이다.
            // 이 세션이 만들어지면 SecurityConfig의 .antMatchers("/api/s/**").authenticated()는 통과 나머지는 role에 의해 달라진다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication); // 강제 로그인
        }
        // JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과는 할 수 있지만, 결국 시큐리티단에서 세션 값 검증에 실패함.

        chain.doFilter(request,response);
    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtVO.HEADER);
        if (header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)) {
            return false;
        } else {
            return true;
        }
    }
}

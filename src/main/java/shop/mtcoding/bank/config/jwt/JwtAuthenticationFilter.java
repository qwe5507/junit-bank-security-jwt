package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.user.UserReqDto.LoginReqDto;
import shop.mtcoding.bank.dto.user.UserResDto.LoginResDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login"); // /api/login으로 변경 (*)
        this.authenticationManager = authenticationManager;
    }

    // Post : "/login"에 동작 함 -> /api/login으로 변경
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");
        try {
            ObjectMapper om = new ObjectMapper();
            // 컨트롤러 가기 전이라서, 이 객체의 username, password에는 Spring Validation 적용 불가
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken); // 내부적으로 UserDetailsService의 loadUserByUsername 호출한다.
            // JWT를 쓴다 하더라도, 컨트롤러 집입 후 시큐리티의 권한체크, 인증체크의 도움을 받기 위해서 세션을 만든다.
            // 이 세션의 유효 기간은 request하고, response하면 끝
            return authentication;
        } catch (Exception e) {
            // unsuccessfulAuthentication 호출함
            // InternalAuthenticationServiceException 에러를 throw해야 unsuccessfulAuthentication이 호출된다.
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.unAuthentication(response, "로그인실패");
    }

    // 로그인 성공
    // 위의 return authentication이 잘 작동하면 해당 메소드가 호출 됩니다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginResDto loginResDto = new LoginResDto(loginUser.getUser());

        CustomResponseUtil.success(response, loginResDto);
    }
}

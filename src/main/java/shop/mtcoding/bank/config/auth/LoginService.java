package shop.mtcoding.bank.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;

    // 시큐리티로 로그인이 될때,
    // 없으면 오류
    // 있으면 시큐리티 컨텍스트 내부 세션에 저장
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("디버그 : loadUserByUsername()");
        User userPS = userRepository.findByUsername(username)
                .orElseThrow(() -> new InternalAuthenticationServiceException("인증 실패")); // 스프링 시큐리티에서 에러발생 시에 이 에러를 발생 시켜야 함

        return new LoginUser(userPS);
    }
}

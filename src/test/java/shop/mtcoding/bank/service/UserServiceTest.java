package shop.mtcoding.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserResDto.JoinResDto;
import shop.mtcoding.bank.dto.user.UserReqDto.JoinReqDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    // 하나의 객체를 선택적으로 stub할 수 있도록 하는 기능이 @Spy(=Mockito.spy) 입니다.
    // 이 객체는 stub하지 않았기 떄문에, 원래 기능이 사용 됨
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_test() {
        // given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("qwe5507@gmail.com");
        joinReqDto.setFullname("쌀쌀");

        // 기대행위를 작성하는 것을 stub이라고 한다.
        // stub 1
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUserName(any())).thenReturn(Optional.of(new User()));

        // stub 2
        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.save(any())).thenReturn(ssar);

        // when
        JoinResDto joinResDto = userService.회원가입(joinReqDto);

        // then
        assertThat(joinResDto.getId()).isEqualTo(1L);
        assertThat(joinResDto.getUsername()).isEqualTo("ssar");
    }

}
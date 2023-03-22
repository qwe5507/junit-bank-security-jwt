package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto.JoinResDto;
import shop.mtcoding.bank.dto.user.UserResDto.JoinReqDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public JoinResDto 회원가입(JoinReqDto joinReqDto) {
        Optional<User> userOp = userRepository.findByUserName(joinReqDto.getUsername());

        if (userOp.isPresent()) {
            throw new CustomApiException("동일한 Username이 존재합니다.");
        }

        // 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        return new JoinResDto(userPS);
    }
}

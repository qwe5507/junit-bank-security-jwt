package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    // dev환경에서 일일이 회원가입하기 귀찮아서 하는 설정
    @Profile("dev") //prod 모드에선 실행되면 안된다.
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return (args) -> {
            // 서버 실행시엔 무조건 실행된다.
            User user = userRepository.save(newUser("ssar", "쌀"));
        };
    }
}

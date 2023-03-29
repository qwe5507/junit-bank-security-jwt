package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    // dev환경에서 일일이 회원가입하기 귀찮아서 하는 설정
    @Profile("dev") //prod 모드에선 실행되면 안된다.
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) { //알아서 DI 해준다.
        System.out.println("DummyDevInit 실행");
        return (args) -> {
            // 서버 실행시엔 무조건 실행된다.
            User ssar = userRepository.save(newUser("ssar", "쌀"));
            User cos = userRepository.save(newUser("cos", "코스"));
            Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
            Account cosAccount1 = accountRepository.save(newAccount(2222L, cos));
        };
    }
}

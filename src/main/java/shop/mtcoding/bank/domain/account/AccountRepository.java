package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // todo : 리팩토링 해야함, fetch join으로 user지 같이 가져오게
    Optional<Account> findByNumber(Long number);

    // 쿼리메소드
    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}

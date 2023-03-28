package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // 필요에 따라 Lazy로딩할지, 페치조인할지 선택
    // 자주 사용하면 굳이 2번 쿼리하는 것보다 페치조인으로 조회
//    @Query("SELECT ac FROM Account ac JOIN FETCH ac.user u WHERE ac.number = :number")
    Optional<Account> findByNumber(Long number);

    // 쿼리메소드
    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}

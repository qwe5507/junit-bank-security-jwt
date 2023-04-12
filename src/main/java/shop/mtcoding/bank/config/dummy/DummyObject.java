package shop.mtcoding.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

    /**
     * @DataJpaTest도 영속성 컨텍스트를 가지고 있기 때문에 더티체킹 되는데
     * 영속 컨텍스트가 없는 컨트롤러 테스트 같은 곳에서 이 클래스를 사용할 수도 있다.
     * 그럴 경우 더티체킹이 동작하지 않는다.
     * 더티체킹이 될지도, 안될지도 모르는 상황이기 떄문에 그냥 수동으로 save
     */

    // 출금 트랜잭션 더미데이터
    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository) {
        account.withdraw(100L); // 1000원이 었다면 1100원이 됨
        // Repository에서는 더티체킹 동작
        // 컨트롤러 Test에서는 더티체킹 안됨, 그래서 아래 처럼 수동으로 save
        if (accountRepository != null) {
            accountRepository.save(account);
        }

        Transaction transaction = Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .withdrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(account.getNumber() + "")
                .receiver("ATM")
                .build();
        return transaction;
    }

    // 입금 트랜잭션 더미데이터
    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository) {
        account.deposit(100L); // 1000원이 었다면 900원이 됨
        // Repository에서는 더티체킹 동작
        // 컨트롤러 Test에서는 더티체킹 안됨, 그래서 아래 처럼 수동으로 save
        if (accountRepository != null) {
            accountRepository.save(account);
        }

        Transaction transaction = Transaction.builder()
                        .withdrawAccount(null)
                        .depositAccount(account)
                        .withdrawAccountBalance(null)
                        .depositAccountBalance(account.getBalance())
                        .amount(100L)
                        .gubun(TransactionEnum.DEPOSIT)
                        .sender("ATM")
                        .receiver(account.getNumber() + "")
                        .tel("01027293256")
                .build();
        return transaction;
    }

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository) {
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);

        // Repository에서는 더티체킹 동작
        // 컨트롤러 Test에서는 더티체킹 안됨, 그래서 아래 처럼 수동으로 save
        if (accountRepository != null) {
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }

        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber() + "")
                .receiver(depositAccount.getNumber() + "")
                .build();
        return transaction;
    }

    protected Transaction newMockDepositTransaction(Long id, Account account) {
        account.deposit(100L); // 계좌에도 입금이 되고, 트랜잭션이 생성되어야 하기 떄문에, 계좌에 100 입금

        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance()) // 입금 당시의 계좌의 금액
                .amount(100L) // 금액 (여기선 입금금액)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01027293256")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return transaction;
    }

    protected User newUser(String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    };

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(UserEnum.CUSTOMER)
                .build();
    };

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}

package shop.mtcoding.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.TransactRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.bank.dto.account.AccountResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountDepositResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountListResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountSaveResDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    private final TransactRepository transactRepository;

    // 각 리스트(List<Account>)에서 Lazy 로딩으로 등록되어있는 유저 객체를 리턴해도 되지만
    // 굳이 모두 같은 값의 User객체를 가지는데, 굳이 Join문을 추가하지 않고 그냥 따로 User객체를 조회하는게 낫다고 함.
    public AccountListResDto 계좌목록보기_유저별(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        // 유저의 모든 계좌목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);

        return new AccountListResDto(userPS, accountListPS);
    }

    @Transactional
    public AccountSaveResDto 계좌등록(AccountSaveReqDto accountSaveReqDto, Long userId) {
        // User가 DB에 있는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        // 해당 계좌가 DB에 있는지 중복여부를 체크
        Optional<Account> accountOpt = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if (accountOpt.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");
        }

        // 계좌 등록
        Account account = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        // DTO를 응답
        return new AccountSaveResDto(account);
    }

    @Transactional
    public void 계좌삭제(Long accountNumber, Long userId) {
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        // 2. 계좌 소유자 인지 확인 (계좌의 userId와 로그인한 userId를 비교 한다.)
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    // 인증이 필요 없다. 그냥 입금이기 때문에
    @Transactional
    public AccountDepositResDto 계좌입금(AccountDepositReqDto accountDepositReqDto) { // ATM -> 누군가의 계좌
        // 0원 체크 (spring validation으로 체크해도 됨)
        if (accountDepositReqDto.getAmount() <= 0) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        // 입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
        depositAccountPS.deposit(accountDepositReqDto.getAmount());
        System.out.println("accountDepositReqDto.getAmount() :: " + depositAccountPS.getBalance());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPS) // 입금계좌
                .withdrawAccount(null) // 출금계좌없음, ATM 기에서 계좌로 입금하는 거니깐
                .depositAccountBalance(depositAccountPS.getBalance()) // 입금 당시의 계좌의 금액
                .withdrawAccountBalance(null)
                .amount(accountDepositReqDto.getAmount()) // 금액 (여기선 입금금액)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber() + "")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactRepository.save(transaction);

        // 입금 성공 응답 Dto 리턴
        return new AccountDepositResDto(depositAccountPS, transactionPS);
    }

    // 로그인이 되어 있어야 함
    @Transactional
    public AccountResDto.AccountWithdrawResDto 계좌출금(AccountReqDto.AccountWithdrawReqDto accountWithdrawReqDto, Long userId) {
        // 0원 체크 (spring validation으로 체크해도 됨)
        if (accountWithdrawReqDto.getAmount() <= 0) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        // 출금 소유자 확인 (로그인한 사람과 계좌의 소유자가 동일한지 확인)
        withdrawAccountPS.checkOwner(userId);

        // 출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());

        // 출금 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        // 거래내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS) // 출금계좌
                .depositAccount(null) // 입금계좌
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount()) // 금액 (여기선 입금금액)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber() + "")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactRepository.save(transaction);

        // DTO 응답
        return new AccountResDto.AccountWithdrawResDto(withdrawAccountPS, transactionPS);
    }
}

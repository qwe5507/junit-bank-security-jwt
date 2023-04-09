package shop.mtcoding.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.TransactRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.bank.dto.account.AccountResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountDepositResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountListResDto;
import shop.mtcoding.bank.dto.account.AccountResDto.AccountSaveResDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactRepository transactRepository;

    @Spy
    private ObjectMapper om;

    @Test
    public void 계좌등록_test() throws Exception {
        // given
        Long userId = 1L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        // stub1
        User ssar = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

        // stub2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        // stub3
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);

        // when
        AccountSaveResDto accountSaveResDto = accountService.계좌등록(accountSaveReqDto, userId);
        String s = om.writeValueAsString(accountSaveResDto);
        System.out.println();

        // then
        assertThat(accountSaveResDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    public void 계좌목록보기_유저별_test() throws Exception {
        // given
        Long userId = 1L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.findById(userId)).thenReturn(Optional.of(ssar));

        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        Account ssarAccount2 = newMockAccount(2L, 2222L, 1000L, ssar);
        List<Account> accountList = Arrays.asList(ssarAccount1, ssarAccount2);
        when(accountRepository.findByUser_id(any())).thenReturn(accountList);

        // when
        AccountListResDto accountListRespDto = accountService.계좌목록보기_유저별(userId);

        // then
        assertThat(accountListRespDto.getFullname()).isEqualTo("쌀");
        assertThat(accountListRespDto.getAccounts().size()).isEqualTo(2);
    }

    @Test
    public void 계좌삭제_test() throws Exception {
        // givne
        Long number = 1111L;
        Long userId = 1L;

        // stub1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        // 실행 로직의 accountRepository.deleteById(accountPS.getId());는 리턴되는게 없기 때문에 stub할 필요 없다
        accountService.계좌삭제(number, userId);
        // when

        // then
//        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));

        // Then
        verify(accountRepository, times(1)).deleteById(ssarAccount.getId());
    }

    // Account -> balance 변경 됐는지
    // Transaction -> balance 잘 기록 됐는지
    @Test
    public void 계좌입금_test() throws Exception {
        // givne
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01027293256");

        // stub1
        User ssar = newMockUser(1L, "ssar", "쌀"); // 실행 됨
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행 됨 - ssarAccount1 계좌 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1)); // 실행 안됨 -> service 호출 후 실행됨 -> ssarAccount1 계좌 1100원

        // stub2 (스텁이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다.)
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2); // 실행 됨 - ssarAccount1 계좌 1100원
        when(transactRepository.save(any())).thenReturn(transaction); // 실행 안됨

        // when
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        System.out.println(accountDepositResDto.getTransaction().getDepositAccountBalance());
        System.out.println(ssarAccount1.getBalance());

        // Then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositResDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    /**
     * 서비스 테스트는 DB와의 연관이 없으므로, 실제 서비스 단의 테스트에서 뭐가 중요한지 생각해야 한다.
     * 위의 테스트 1에서 accountDepositResDto의 값을 검증할 필요가 있는가? Stub으로 생성된 객체이므로 필요 없다.
     * -> 계좌 입금 로직을 잘보면, 서비스단에서 체크해야할 부분은 아래와 같다.
     *  1. 0원 체크
     *  2. deposit (account1의 계정이 입금되었는지)
     *  3. Dto가 잘 생성 되는지
     *  이 부분에 집중하여 테스트를 작성해야 한다.
     *
     *  Dto가 잘 만들어 졌는지는, Controller테스트 에서도 확인할 수 있기 때문에,
     *  1번 2번에 대한것 만 검증
     */
    @Test
    public void 계좌입금_test2() throws Exception {
        // givne
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01027293256");

        // stub1
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

        // stub2
        User ssar2 = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar2);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactRepository.save(any())).thenReturn(transaction);

        // when
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositResDto);
        System.out.println("테스트 : " + responseBody);

        // then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L); // account2는 stub으로 생성된 객체이기 떄문에 실제 입금 로직의 대상이 된 account1의 값을 검증
    }

    /**
     * 실제 서비스에서 테스트 해야 할것은 아래와 같고, Db의 결과에 따라 로직이 달라지는게 아니기때문에 stub을 만들 필요도 없음.
     *   1. 0원 체크
     *   2. deposit (account1의 계정이 입금되었는지)
     * 아래와 같이 간단하게 테스트 할 수 있다. (Dto생성에 대한것은 Controller테스트에 위임)
     *
     * 이런 방식으로 테스트 해도 된다.
     */
    @Test
    public void 계좌입금_test3() throws Exception {
        // given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        // when
        if (amount <= 0) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        account.deposit(100L);

        // then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }

    // 계좌 출금_테스트

    // 계좌 이체_테스트

    // 계좌 목록 보기_테스트

    // 계좌 상세보기_테스트
}
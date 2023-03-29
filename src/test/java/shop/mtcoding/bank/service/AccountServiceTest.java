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

        // stub2
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount1); // 실행 됨 - ssarAccount1 계좌 1100원
        when(transactRepository.save(any())).thenReturn(transaction); // 실행 안됨

        // when
        AccountDepositResDto accountDepositResDto = accountService.계좌입금(accountDepositReqDto);
        System.out.println(accountDepositResDto.getTransaction().getDepositAccountBalance());
        System.out.println(ssarAccount1.getBalance());
        // Then
    }
}
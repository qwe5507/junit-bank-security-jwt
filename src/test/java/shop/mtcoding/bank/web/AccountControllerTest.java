package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountTransferReqDto;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountWithdrawReqDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql") // 실행시점 , @BeforeEach 실행 직전 마다
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스"));
        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount1 = accountRepository.save(newAccount(2222L, cos));
        em.clear();
    }

    // 테스트를 위해 jwt 토큰을 넣어줄 필요는 없고 시큐리티 세션만 넣어주면 된다.
    // setupBefore=TEST_METHOD (디폴트 옵션, WithUserDetails가 위의 setup() 메소드 실행 되기 전에 수행된다. // setUp()에서 ssar유저를 넣기 전에 loadUserByUsername()에서 실패 발생)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행 직전 전에 수행 // setUp()실행해서 ssar유저가 insert되고 난 후, 시큐리티 인증 후 saveAccount_test() 테스트 실행)
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @WithUserDetails(value = "ssar")
    // 디비에서 username=ssar 조회를 해서 세션에 담아주는 애노테이션, loadUserByUsername()를 실행시킨다.
    @Test
    public void saveAccount_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountSaveReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String repsonseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + repsonseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findUserAccount_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/s/account/login-user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    /**
     * - 실제와 동일하게 setup()의 insert하고 영속 컨텍스트에 저장된 것 초기화
     */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test() throws Exception {
        // given
        Long number = 1111L;

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")));
        // Junit의 맨 마지막 쿼리는 발생하지 않는다고 함(?),
        // -> 근데 롤백 때문인거 같음..
    }

    @Test
    public void depositAccount_test() throws Exception {
        // givne
        AccountReqDto.AccountDepositReqDto accountDepositReqDto = new AccountReqDto.AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01027293256");

        String requestBody = om.writeValueAsString(accountDepositReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/account/deposit")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        // 계좌의 잔액이 추가되는건 Service test에서 테스트 했기 때문에, Dto가 잘만들어 졌는지(성공처리 되었는지)만 테스트
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdrawAccount_test() throws Exception {
        // given
        AccountWithdrawReqDto reqDto = new AccountWithdrawReqDto();
        reqDto.setNumber(1111L);
        reqDto.setPassword(1234L);
        reqDto.setAmount(100L);
        reqDto.setGubun("WITHDRAW");

        String requestBody = om.writeValueAsString(reqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/s/account/withdraw")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }

    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void transferAccount_test() throws Exception {
        // given
        AccountTransferReqDto reqDto = new AccountTransferReqDto();
        reqDto.setWithdrawNumber(1111L);
        reqDto.setDepositNumber(2222L);
        reqDto.setWithdrawPassword(1234L);
        reqDto.setAmount(100L);
        reqDto.setGubun("TRANSFER");

        String requestBody = om.writeValueAsString(reqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/s/account/transfer")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }
}
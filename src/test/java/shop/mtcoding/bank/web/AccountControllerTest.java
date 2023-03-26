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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
    }

    // 테스트를 위해 jwt 토큰을 넣어줄 필요는 없고 시큐리티 세션만 넣어주면 된다.
    // setupBefore=TEST_METHOD (디폴트 옵션, WithUserDetails가 위의 setup() 메소드 실행 되기 전에 수행된다. // setUp()에서 ssar유저를 넣기 전에 시큐리티 검증해서 실패 난다는 뜻임)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행 직전 전에 수행 // setUp()실행해서 ssar유저가 insert되고 난 후, 시큐리티 인증 후 saveAccount_test() 테스트 실행)
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // 디비에서 username=ssar 조회를 해서 세션에 담아주는 애노테이션
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
}
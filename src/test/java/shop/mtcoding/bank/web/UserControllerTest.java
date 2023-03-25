package shop.mtcoding.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto.JoinReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        dataSetting();
    }

    @Test
    public void join_success_test() throws Exception {
        // given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("love");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("love@nate.com");
        joinReqDto.setFullname("러브");

        String requestBody = objectMapper.writeValueAsString(joinReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated()); // 201
    }

    @Test
    public void join_fail_test() throws Exception {
        // given
        userRepository.save(newUser("ssar", "쌀"));
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("ssar@nate.com");
        joinReqDto.setFullname("쌀");

        String requestBody = objectMapper.writeValueAsString(joinReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()); // 400
    }

    private void dataSetting() {

    }
}
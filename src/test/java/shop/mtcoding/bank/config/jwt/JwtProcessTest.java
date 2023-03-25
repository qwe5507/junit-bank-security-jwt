package shop.mtcoding.bank.config.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProcessTest {

    @Test
    public void create_test() {
        // given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);

        // when
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println(jwtToken);

        // then
        assertThat(jwtToken.startsWith(JwtVO.TOKEN_PREFIX)).isTrue();
    }

    @Test
    public void verify_test() {
        // given
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5rIiwicm9sZSI6IkNVU1RPTUVSIiwiaWQiOjEsImV4cCI6MTY4MDMzNDI1OH0.Oj0M0dAMaUE_pCZRlsWp6Kb1QpGKVa2mQvt3i8zKjJfwxkcg4rg3_u2tbXzUwxJnTHLeTJHBstmUo0arXO4QYA";

        // when
        LoginUser loginUser = JwtProcess.verify(jwtToken);

        // then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.CUSTOMER);
    }
}
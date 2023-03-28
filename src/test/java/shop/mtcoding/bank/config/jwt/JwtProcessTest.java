package shop.mtcoding.bank.config.jwt;

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

    private String createToken() {
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser1 = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser1);
        return jwtToken;
    }

    @Test
    public void verify_test() {
        // given
        String token = createToken();

        // when
        LoginUser loginUser2 = JwtProcess.verify(token.replace(JwtVO.TOKEN_PREFIX, ""));

        // then
        assertThat(loginUser2.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser2.getUser().getRole()).isEqualTo(UserEnum.CUSTOMER);
    }
}
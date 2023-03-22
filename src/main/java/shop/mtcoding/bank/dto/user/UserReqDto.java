package shop.mtcoding.bank.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;

public class UserReqDto {
    @ToString
    @Setter
    @Getter
    @NoArgsConstructor
    public static class JoinResDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinResDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}

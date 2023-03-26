package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class AccountReqDto {
    @Getter
    @Setter
    public static class AccountSaveReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4) // Long타입은 Digits로 validation, 최소4자, 최대 4자
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L) //default 잔액 1000원
                    .user(user)
                    .build();
        }
    }
}

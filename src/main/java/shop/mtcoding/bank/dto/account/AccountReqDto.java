package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

    @Getter
    @Setter
    public static class AccountDepositReqDto { // ATM -> 계좌 , 입금 요청 객체
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$") // DEPOSIT만 가능, []는 범위, 정확하게 지정할떈 ()
        private String gubun; // DEPOSIT
        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }
}

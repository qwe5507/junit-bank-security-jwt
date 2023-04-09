package shop.mtcoding.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountResDto {
    @Getter
    @Setter
    public static class AccountSaveResDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveResDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Getter
    @Setter
    public static class AccountListResDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListResDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositResDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private TransactionDto transaction; // 이런 로그가 남았다.

        public AccountDepositResDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
        }

        // ATM기에 입금했다고 계좌의 남은 금액 같은 중요정보가 보여선 안된다.
        @Getter
        @Setter
        public class TransactionDto { // 무슨로그가 남았는지
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance; // 입금이니 클라이언트에게 전달 X - 서비스단 테스트 용도로 @JsonIgnore
            private String tel;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountWithdrawResDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private Long balance; // 잔액
        private TransactionDto transaction;

        public AccountWithdrawResDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto { // 무슨로그가 남았는지
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountTransferResDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private Long balance; // 출금계좌 잔액
        private TransactionDto transaction;

        public AccountTransferResDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto { // 무슨로그가 남았는지
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;

            @JsonIgnore
            private Long depositAccountBalance; // 입금 계좌 잔액
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}

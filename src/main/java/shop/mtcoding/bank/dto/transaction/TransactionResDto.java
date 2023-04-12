package shop.mtcoding.bank.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionResDto {
    @Setter
    @Getter
    public static class TransactionListResDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListResDto(Account account, List<Transaction> transactions) {
            this.transactions =  transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList());
        }
        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private Long amount;
            private String sender;
            private String receiver;
            private String tel;
            private String createdAt;
            private Long balance;

            public TransactionDto(Transaction transaction, Long accountNumber) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel(); // 계좌 입금일떄만 있음

                // 1111 계좌의 입금이나(출금계좌 = null, 입금계좌 = 값) 출금 (출금계좌 = 값, 입금계좌 = null)내역
                if (transaction.getDepositAccount() == null) {
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null){
                    this.balance = transaction.getDepositAccountBalance();
                } else { // 입출금 내역 조회
                    // 1111 계좌의 입출금 내역 (출금계좌 = 값, 입금계좌 = 값)
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
                        this.balance = transaction.getDepositAccountBalance();
                    } else {
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}

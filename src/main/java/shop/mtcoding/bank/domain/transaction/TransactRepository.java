package shop.mtcoding.bank.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactRepository extends JpaRepository<Transaction, Long> {
}

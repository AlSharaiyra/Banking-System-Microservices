package com.warba.banking.account.repository.repo;

import com.warba.banking.account.repository.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findByAccountNumber(Long accountNumber);
}

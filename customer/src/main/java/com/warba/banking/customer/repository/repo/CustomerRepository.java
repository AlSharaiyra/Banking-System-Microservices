package com.warba.banking.customer.repository.repo;

import com.warba.banking.customer.repository.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository <Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);

    Optional<Customer> findByLegalId(String legalId);

    Optional<Customer> findByMobileNumber(String mobileNumber);
}

package com.warba.banking.customer.repository.repo;

import com.warba.banking.customer.repository.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

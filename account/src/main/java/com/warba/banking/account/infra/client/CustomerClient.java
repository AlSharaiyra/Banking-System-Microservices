package com.warba.banking.account.infra.client;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "customer-service", path = "/api/v1/customer/service/customers/accounts")
public interface CustomerClient {

    @GetMapping("/{customerId}")
    CustomerDetailsDTO getCustomerDetails(@PathVariable Long customerId);

    @PutMapping("/{customerId}")
    ResponseEntity<Void> updateCustomer(@PathVariable Long customerId, @RequestBody @Valid CustomerDetailsDTO request);
}

package com.warba.banking.customer.controller;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.customer.service.AccountManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Tag(name = "Account Management",
        description = "A set of APIs to perform management on bank accounts and communication with account-service.")
@RestController
@RequestMapping("/customers/accounts")
@RequiredArgsConstructor
public class AccountManagementController {
    private final AccountManagementService accountManagementService;

    @Operation(description = """
            An API call to retrieve customer details to the account-service.
            """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{customerId}")
    public CustomerDetailsDTO getCustomerDetails(@PathVariable Long customerId) {
        return accountManagementService.getCustomerDetails(customerId);
    }

    @Operation(description = """
            An API call to update the customer after creating a new account.
            """)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable Long customerId, @RequestBody @Valid CustomerDetailsDTO request) {
        log.debug("Updating customer with ID: {} with totalAccounts: {}, and hasSalaryAccount: {}",
                customerId, request.totalAccounts(), request.hasSalaryAccount());

        accountManagementService.updateCustomer(customerId, request);

        return ResponseEntity.ok().build();
    }
}

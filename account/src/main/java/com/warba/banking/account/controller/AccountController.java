package com.warba.banking.account.controller;

import com.warba.banking.account.model.request.CreateAccountRequest;
import com.warba.banking.account.model.request.UpdateAccountStatusRequest;
import com.warba.banking.account.model.response.AccountResponse;
import com.warba.banking.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Tag(name = "Accounts",
        description = "A set of APIs to perform management and operations on customers` bank accounts and their related data.")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(description = """
            An API call to create a new bank account for a customer.
            Checks for existence of customer, and validates the ability to create a new bank account for that customer.
            """)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{customerId}")
    public ResponseEntity<AccountResponse> createAccount(@PathVariable Long customerId, @Valid @RequestBody CreateAccountRequest request){
        log.debug("Creating new bank account of type: {}, for customer with ID: {}", request.accountType(), customerId);

        AccountResponse response = accountService.createNewAccount(customerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(description = """
            An API call to get all customer`s accounts.
            """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customer/{customerId}")
    public List<AccountResponse> getCustomerAccounts(@PathVariable Long customerId){
        log.debug("Retrieving all accounts for customer with ID: {}", customerId);

        return accountService.getCustomerAccounts(customerId);
    }

    @Operation(description = """
            An API call to get a customer account.
            """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{accountNo}")
    public AccountResponse getAccount(@PathVariable Long accountNo){
        log.debug("Retrieving account with account number: {}", accountNo);

        return accountService.getAccount(accountNo);
    }

    @Operation(description = """
            An API call to update account status.
            """)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{accountNo}")
    public AccountResponse updateAccountStatus(@PathVariable Long accountNo, @RequestBody @Valid UpdateAccountStatusRequest request){
        log.debug("Updating account status with account number: {}", accountNo);

        return accountService.updateAccountStatus(accountNo, request);
    }
}

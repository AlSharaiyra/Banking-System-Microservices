package com.warba.banking.account.service;

import com.warba.banking.account.producer.RabbitMQProducer;
import com.warba.banking.account.infra.client.CustomerClient;
import com.warba.banking.account.infra.exception.*;
import com.warba.banking.account.infra.util.AccountNumberGenerator;
import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.account.model.enums.AccountStatus;
import com.warba.banking.account.model.enums.AccountType;
import com.warba.banking.account.model.mapper.AccountMapper;
import com.warba.banking.account.model.request.CreateAccountRequest;
import com.warba.banking.account.model.request.UpdateAccountStatusRequest;
import com.warba.banking.account.model.response.AccountResponse;
import com.warba.banking.account.repository.entity.Account;
import com.warba.banking.account.repository.repo.AccountRepository;
import com.warba.banking.common.event.AccountClosedEvent;
import com.warba.banking.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final AccountMapper accountMapper;
    private final RabbitMQProducer rabbitMQProducer;

    private static final Integer MIN_INVESTMENT_ACC_BALANCE = 3000;
    private static final Integer MAX_NO_OF_ACCOUNTS = 10;

    /**
     * This method is responsible for creating a new bank account.
     *
     * @param customerId of customer account to be created
     * @param request    data to create a new account.
     * @return AccountResponse of created account.
     */
    @Transactional
    public AccountResponse createNewAccount(Long customerId, CreateAccountRequest request) {
        // Fetch all customer-related details in ONE API call
        CustomerDetailsDTO customerDetails = customerClient.getCustomerDetails(customerId);

        if (customerDetails == null)
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);

        Integer noOfAccounts = customerDetails.totalAccounts();
        Boolean hasSalaryAccount = customerDetails.hasSalaryAccount();

        // Business logic/rules validations
        if (noOfAccounts >= MAX_NO_OF_ACCOUNTS)
            throw new MaxNoOfAccountsException("Customer with ID: {" + customerId +
                    "} cannot open a new account. Maximum number of accounts reached.");

        if (hasSalaryAccount && request.accountType().equals(AccountType.SALARY))
            throw new SalaryAccountExistsException("Customer with ID: {" + customerId +
                    "} has an active Salary account already, not allowed to create more than one salary account.");

        if (request.accountType().equals(AccountType.INVESTMENT) && request.balance().compareTo(BigDecimal.valueOf(MIN_INVESTMENT_ACC_BALANCE)) < 0)
            throw new MinAccountBalanceException("Investment account balance must be equal to or larger than: " + MIN_INVESTMENT_ACC_BALANCE);

        // Create account and set fields
        Account account = accountMapper.toAccount(request);
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber(customerId, request.accountType(), noOfAccounts + 1));
        account.setCustomerId(customerId);
        account.setAccountStatus(AccountStatus.ACTIVE);

        // Update customer data before saving the account
        CustomerDetailsDTO updateRequest = new CustomerDetailsDTO(
                request.accountType().equals(AccountType.SALARY) || customerDetails.hasSalaryAccount(),
                noOfAccounts + 1
        );

        ResponseEntity<Void> response = customerClient.updateCustomer(customerId, updateRequest);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new AccountCreationFailedException("Failed to update customer, aborting account creation.");

        // Try to save the account
        try {
            log.debug("Trying to save the newly created account.");
            accountRepository.save(account);
        } catch (Exception e) {
            log.error("Failed to update customer details with customer ID: {}.", customerId);

            // Rollback customer update
            CustomerDetailsDTO rollbackRequest = new CustomerDetailsDTO(
                    customerDetails.hasSalaryAccount(),
                    noOfAccounts
            );
            customerClient.updateCustomer(customerId, rollbackRequest);

            throw new AccountCreationFailedException("Failed to create account, rolling back customer update.");
        }

        log.info("New account of type {} is created for customer with ID: {}", request.accountType(), customerClient);

        return accountMapper.toAccountView(account);
    }

    /**
     * This method is responsible for retrieving an existing account.
     *
     * @param accountNumber of account to be retrieved.
     * @return AccountResponse of account to be retrieved.
     */
    public AccountResponse getAccount(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        log.info("Account found with account number: {}", accountNumber);

        return accountMapper.toAccountView(account);
    }

    /**
     * This method is responsible for updating the status of a bank account.
     *
     * @param accountNumber of account to be updated
     * @return AccountResponse of account to be updated.
     */
    public AccountResponse updateAccountStatus(Long accountNumber, UpdateAccountStatusRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));

        account.setAccountStatus(request.accountStatus());

        accountRepository.save(account);

        // Publish RabbitMQ event to update the totalAccount in customer-service
        if (request.accountStatus().equals(AccountStatus.CLOSED)){

            AccountClosedEvent event = new AccountClosedEvent(account.getCustomerId(), account.getAccountType().equals(AccountType.SALARY));
            rabbitMQProducer.sendCloseAccount(event);
        }

        log.info("Account status updated to: {}, account number: {}", request.accountStatus(), accountNumber);

        return accountMapper.toAccountView(account);
    }

    /**
     * This method is responsible for retrieving customer bank accounts.
     *
     * @param customerId of customer account to be created
     * @return List<AccountResponse> of accounts owned by customer.
     */
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        List<AccountResponse> accounts =
                accountRepository.findByCustomerId(customerId).stream().map(accountMapper::toAccountView).toList();

        if (accounts.isEmpty())
            throw new ResourceNotFoundException("No accounts found for customer with ID: " + customerId);

        return accounts;
    }

}

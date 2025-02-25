package com.warba.banking.account.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.warba.banking.account.infra.client.CustomerClient;
import com.warba.banking.account.infra.exception.AccountCreationFailedException;
import com.warba.banking.account.infra.exception.MaxNoOfAccountsException;
import com.warba.banking.account.infra.exception.MinAccountBalanceException;
import com.warba.banking.account.infra.exception.SalaryAccountExistsException;
import com.warba.banking.account.model.enums.AccountStatus;
import com.warba.banking.account.model.enums.AccountType;
import com.warba.banking.account.model.mapper.AccountMapper;
import com.warba.banking.account.model.request.CreateAccountRequest;
import com.warba.banking.account.model.request.UpdateAccountStatusRequest;
import com.warba.banking.account.model.response.AccountResponse;
import com.warba.banking.account.producer.RabbitMQProducer;
import com.warba.banking.account.repository.entity.Account;
import com.warba.banking.account.repository.repo.AccountRepository;
import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.common.event.AccountClosedEvent;
import com.warba.banking.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private AccountService accountService;

    private final Long customerId = 1234567L;

    // --- createNewAccount tests ---

    @Test
    void testCreateNewAccount_success_investment() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.INVESTMENT);
        // Customer has no accounts and no salary account
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 0);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        Account account = new Account();
        when(accountMapper.toAccount(request)).thenReturn(account);

        // Simulate successful update of customer details
        when(customerClient.updateCustomer(eq(customerId), any()))
                .thenReturn(ResponseEntity.ok().build());

        // Simulate repository saving by setting some fields
        when(accountRepository.save(account)).thenAnswer(invocation -> {
            account.setId(1L);
            account.setRegistrationDate(LocalDateTime.now());
            account.setLastModified(LocalDateTime.now());
            return account;
        });

        AccountResponse expectedResponse = new AccountResponse(
                1234567890L,
                customerId,
                request.balance(),
                request.accountType(),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(accountMapper.toAccountView(account)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountService.createNewAccount(customerId, request);

        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.customerId());
        assertEquals(request.accountType(), result.accountType());
        verify(customerClient, times(1)).updateCustomer(eq(customerId), any());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testCreateNewAccount_customerNotFound() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.INVESTMENT);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void testCreateNewAccount_maxAccountsExceeded() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(1000), AccountType.SAVING);
        // Simulate customer already having maximum accounts (10)
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 10);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        // Act & Assert
        MaxNoOfAccountsException exception = assertThrows(MaxNoOfAccountsException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("Maximum number of accounts reached"));
    }

    @Test
    void testCreateNewAccount_salaryAccountExists() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(1000), AccountType.SALARY);
        // Simulate customer already having a salary account
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(true, 1);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        // Act & Assert
        SalaryAccountExistsException exception = assertThrows(SalaryAccountExistsException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("has an active Salary account already"));
    }

    @Test
    void testCreateNewAccount_investmentBalanceTooLow() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(2000), AccountType.INVESTMENT);
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 0);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        // Act & Assert
        MinAccountBalanceException exception = assertThrows(MinAccountBalanceException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("Investment account balance must be equal to or larger than"));
    }

    @Test
    void testCreateNewAccount_updateCustomerFails() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.INVESTMENT);
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 0);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        Account account = new Account();
        when(accountMapper.toAccount(request)).thenReturn(account);

        // Simulate update customer failure by returning a non-2xx response
        when(customerClient.updateCustomer(eq(customerId), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        // Act & Assert
        AccountCreationFailedException exception = assertThrows(AccountCreationFailedException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("Failed to update customer"));
    }

    @Test
    void testCreateNewAccount_saveAccountFails() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.SAVING);
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 0);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        Account account = new Account();
        when(accountMapper.toAccount(request)).thenReturn(account);

        // Simulate successful update customer
        when(customerClient.updateCustomer(eq(customerId), any()))
                .thenReturn(ResponseEntity.ok().build());

        // Simulate repository save failure
        when(accountRepository.save(account)).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        AccountCreationFailedException exception = assertThrows(AccountCreationFailedException.class,
                () -> accountService.createNewAccount(customerId, request));
        assertTrue(exception.getMessage().contains("Failed to create account"));
        // Verify that a rollback update is attempted (i.e. updateCustomer is called twice)
        verify(customerClient, times(2)).updateCustomer(eq(customerId), any());
    }

    // --- getAccount tests ---

    @Test
    void testGetAccount_success() {
        // Arrange
        Long accountNumber = 1234567890L;
        Account account = new Account();
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountResponse expectedResponse = new AccountResponse(
                accountNumber,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(accountMapper.toAccountView(account)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountService.getAccount(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(accountNumber, result.accountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    void testGetAccount_notFound() {
        // Arrange
        Long accountNumber = 1234567890L;
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.getAccount(accountNumber));
        assertTrue(exception.getMessage().contains("Account not found"));
    }

    // --- updateAccountStatus tests ---

    @Test
    void testUpdateAccountStatus_success_closed() {
        // Arrange
        Long accountNumber = 1234567890L;
        Account account = new Account();
        account.setCustomerId(customerId);
        // For testing a CLOSED event, assume the account type is SALARY
        account.setAccountType(AccountType.SALARY);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.CLOSED);
        AccountResponse expectedResponse = new AccountResponse(
                accountNumber,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SALARY,
                AccountStatus.CLOSED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(accountMapper.toAccountView(account)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountService.updateAccountStatus(accountNumber, request);

        // Assert
        assertNotNull(result);
        assertEquals(AccountStatus.CLOSED, result.accountStatus());
        verify(accountRepository, times(1)).save(account);
        // Verify that a RabbitMQ event is published
        verify(rabbitMQProducer, times(1)).sendCloseAccount(any(AccountClosedEvent.class));
    }

    @Test
    void testUpdateAccountStatus_success_nonClosed() {
        // Arrange
        Long accountNumber = 1234567890L;
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountType(AccountType.SAVING);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.SUSPENDED);
        AccountResponse expectedResponse = new AccountResponse(
                accountNumber,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.SUSPENDED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(accountMapper.toAccountView(account)).thenReturn(expectedResponse);

        // Act
        AccountResponse result = accountService.updateAccountStatus(accountNumber, request);

        // Assert
        assertNotNull(result);
        assertEquals(AccountStatus.SUSPENDED, result.accountStatus());
        verify(accountRepository, times(1)).save(account);
        // Verify that no RabbitMQ event is sent since status is not CLOSED
        verify(rabbitMQProducer, never()).sendCloseAccount(any(AccountClosedEvent.class));
    }

    @Test
    void testUpdateAccountStatus_accountNotFound() {
        // Arrange
        Long accountNumber = 1234567890L;
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.CLOSED);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.updateAccountStatus(accountNumber, request));
        assertTrue(exception.getMessage().contains("Account not found"));
    }

    // --- getCustomerAccounts tests ---

    @Test
    void testGetCustomerAccounts_success() {
        // Arrange
        Account account = new Account();
        AccountResponse accountResponse = new AccountResponse(
                1234567890L,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of(account));
        when(accountMapper.toAccountView(account)).thenReturn(accountResponse);

        // Act
        List<AccountResponse> results = accountService.getCustomerAccounts(customerId);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(accountRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testGetCustomerAccounts_empty() {
        // Arrange
        when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> accountService.getCustomerAccounts(customerId));
        assertTrue(exception.getMessage().contains("No accounts found"));
    }

    @Test
    void testCreateNewAccount_databaseSaveFails() {
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.SAVING);
        CustomerDetailsDTO customerDetails = new CustomerDetailsDTO(false, 0);
        when(customerClient.getCustomerDetails(customerId)).thenReturn(customerDetails);

        Account account = new Account();
        when(accountMapper.toAccount(request)).thenReturn(account);

        when(customerClient.updateCustomer(eq(customerId), any())).thenReturn(ResponseEntity.ok().build());

        when(accountRepository.save(account)).thenThrow(new RuntimeException("Database save error"));

        AccountCreationFailedException exception = assertThrows(AccountCreationFailedException.class,
                () -> accountService.createNewAccount(customerId, request));

        assertTrue(exception.getMessage().contains("Failed to create account"));
    }


}

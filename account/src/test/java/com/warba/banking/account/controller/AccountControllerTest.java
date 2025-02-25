package com.warba.banking.account.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.warba.banking.account.controller.AccountController;
import com.warba.banking.account.model.enums.AccountStatus;
import com.warba.banking.account.model.enums.AccountType;
import com.warba.banking.account.model.request.CreateAccountRequest;
import com.warba.banking.account.model.request.UpdateAccountStatusRequest;
import com.warba.banking.account.model.response.AccountResponse;
import com.warba.banking.account.service.AccountService;
import com.warba.banking.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@Import(AccountControllerTest.AccountServiceTestConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService; // This bean is defined in our TestConfiguration

    @Autowired
    private ObjectMapper objectMapper; // Useful for JSON serialization

    @TestConfiguration
    static class AccountServiceTestConfig {
        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
    }

    @Test
    void testCreateAccount_success() throws Exception {
        Long customerId = 1234567L;
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.INVESTMENT);
        AccountResponse response = new AccountResponse(
                1234567890L,
                customerId,
                BigDecimal.valueOf(4000),
                AccountType.INVESTMENT,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(accountService.createNewAccount(eq(customerId), any())).thenReturn(response);

        mockMvc.perform(post("/accounts/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value(response.accountNumber()))
                .andExpect(jsonPath("$.customerId").value(response.customerId()))
                .andExpect(jsonPath("$.balance").value(response.balance().doubleValue()))
                .andExpect(jsonPath("$.accountType").value("INVESTMENT"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));
    }

    @Test
    void testGetAccount_success() throws Exception {
        Long accountNumber = 1234567890L;
        Long customerId = 1234567L;
        AccountResponse response = new AccountResponse(
                accountNumber,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(accountService.getAccount(accountNumber)).thenReturn(response);

        mockMvc.perform(get("/accounts/{accountNo}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.customerId").value(customerId));
    }

    @Test
    void testGetCustomerAccounts_success() throws Exception {
        Long customerId = 1234567L;
        AccountResponse response1 = new AccountResponse(
                1234567890L,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        AccountResponse response2 = new AccountResponse(
                1234567891L,
                customerId,
                BigDecimal.valueOf(3000),
                AccountType.SALARY,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(accountService.getCustomerAccounts(customerId)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/accounts/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value(response1.accountNumber()))
                .andExpect(jsonPath("$[1].accountNumber").value(response2.accountNumber()));
    }

    @Test
    void testUpdateAccountStatus_success() throws Exception {
        Long accountNumber = 1234567890L;
        Long customerId = 1234567L;
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.SUSPENDED);
        AccountResponse response = new AccountResponse(
                accountNumber,
                customerId,
                BigDecimal.valueOf(5000),
                AccountType.SAVING,
                AccountStatus.SUSPENDED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(accountService.updateAccountStatus(eq(accountNumber), any())).thenReturn(response);

        mockMvc.perform(put("/accounts/{accountNo}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("SUSPENDED"));
    }

    @Test
    void testCreateAccount_customerNotFound() throws Exception {
        Long customerId = 1234567L;
        CreateAccountRequest request = new CreateAccountRequest(BigDecimal.valueOf(4000), AccountType.INVESTMENT);

        when(accountService.createNewAccount(eq(customerId), any()))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(post("/accounts/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    void testUpdateAccountStatus_accountNotFound() throws Exception {
        Long accountNumber = 1234567890L;
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.CLOSED);

        when(accountService.updateAccountStatus(eq(accountNumber), any()))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(put("/accounts/{accountNo}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

}

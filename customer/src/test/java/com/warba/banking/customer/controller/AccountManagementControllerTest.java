package com.warba.banking.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.customer.service.AccountManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagementControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AccountManagementService accountManagementService;

    @InjectMocks
    private AccountManagementController accountManagementController;

    private CustomerDetailsDTO customerDetailsDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountManagementController).build();
        // Assume CustomerDetailsDTO has two fields: hasSalaryAccount and totalAccounts.
        customerDetailsDTO = new CustomerDetailsDTO(true, 5);
    }

    @Test
    void testGetCustomerDetails() throws Exception {
        when(accountManagementService.getCustomerDetails(1234567L)).thenReturn(customerDetailsDTO);
        mockMvc.perform(get("/customers/accounts/{customerId}", 1234567)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasSalaryAccount", is(true)))
                .andExpect(jsonPath("$.totalAccounts", is(5)));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        // This endpoint returns an empty response with HTTP 200 OK.
        mockMvc.perform(put("/customers/accounts/{customerId}", 1234567)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDetailsDTO)))
                .andExpect(status().isOk());
    }
}

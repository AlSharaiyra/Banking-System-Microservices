package com.warba.banking.customer.service;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.common.exceptions.ResourceNotFoundException;
import com.warba.banking.customer.model.enums.CustomerType;
import com.warba.banking.customer.model.mapper.CustomerMapper;
import com.warba.banking.customer.repository.entity.Address;
import com.warba.banking.customer.repository.entity.Customer;
import com.warba.banking.customer.repository.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private AccountManagementService accountManagementService;

    private Customer customer;
    private CustomerDetailsDTO customerDetailsDTO;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1234567L);
        customer.setName("Test Customer");
        customer.setLegalId("1234567890");
        customer.setCustomerType(CustomerType.RETAIL);
        customer.setMobileNumber("0123456789");
        customer.setTotalAccounts(5);
        customer.setHasSalaryAccount(true);
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setLastModified(LocalDateTime.now());
        customer.setAddress(new Address(1L, "Home", "Country", "City", "Street", 10));

        // Assume CustomerDetailsDTO has two fields: hasSalaryAccount and totalAccounts.
        customerDetailsDTO = new CustomerDetailsDTO(true, 5);
    }

    @Test
    void testGetCustomerDetails_Success() {
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        when(customerMapper.customerDetailsDTO(customer)).thenReturn(customerDetailsDTO);

        CustomerDetailsDTO result = accountManagementService.getCustomerDetails(1234567L);
        assertNotNull(result);
        assertTrue(result.hasSalaryAccount());
        assertEquals(5, result.totalAccounts());
    }

    @Test
    void testGetCustomerDetails_NotFound() {
        when(customerRepository.findById(1234567L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> accountManagementService.getCustomerDetails(1234567L));
        assertEquals("Customer not found with ID: 1234567", ex.getMessage());
    }

    @Test
    void testUpdateCustomer_Success() {
        CustomerDetailsDTO newDetails = new CustomerDetailsDTO(false, 6);
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        accountManagementService.updateCustomer(1234567L, newDetails);
        assertFalse(customer.getHasSalaryAccount());
        assertEquals(6, customer.getTotalAccounts());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testHandleAccountClosed_SalaryAccount() {
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        accountManagementService.handleAccountClosed(1234567L, true);
        // When a salary account is closed, hasSalaryAccount becomes false and totalAccounts decrements.
        assertFalse(customer.getHasSalaryAccount());
        assertEquals(4, customer.getTotalAccounts());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testHandleAccountClosed_NonSalaryAccount() {
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        accountManagementService.handleAccountClosed(1234567L, false);
        // hasSalaryAccount remains true but totalAccounts decrements.
        assertTrue(customer.getHasSalaryAccount());
        assertEquals(4, customer.getTotalAccounts());
        verify(customerRepository, times(1)).save(customer);
    }
}

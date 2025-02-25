package com.warba.banking.customer.service;

import com.warba.banking.common.exceptions.ResourceNotFoundException;
import com.warba.banking.customer.infra.exception.DuplicateResourceException;
import com.warba.banking.customer.model.enums.CustomerType;
import com.warba.banking.customer.model.mapper.AddressMapper;
import com.warba.banking.customer.model.mapper.CustomerMapper;
import com.warba.banking.customer.model.request.CreateAddressRequest;
import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.request.UpdateAddressRequest;
import com.warba.banking.customer.model.request.UpdateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private CustomerService customerService;

    private CreateCustomerRequest createCustomerRequest;
    private Address address;
    private Customer customer;
    private CustomerResponse customerResponse;

    @BeforeEach
    public void setUp() {
        createCustomerRequest = new CreateCustomerRequest(
                "Test Customer",
                "1234567890",
                CustomerType.RETAIL,
                "0123456789",
                new CreateAddressRequest("Home", "Country", "City", "Street", 10)
        );
        address = new Address(1L, "Home", "Country", "City", "Street", 10);
        customer = new Customer();
        customer.setId(1234567L); // 7 digits as required
        customer.setName("Test Customer");
        customer.setLegalId("1234567890");
        customer.setCustomerType(CustomerType.RETAIL);
        customer.setMobileNumber("0123456789");
        // The service will set the address, totalAccounts, and hasSalaryAccount
        customer.setTotalAccounts(0);
        customer.setHasSalaryAccount(false);
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setLastModified(LocalDateTime.now());

        customerResponse = new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getLegalId(),
                customer.getCustomerType(),
                customer.getMobileNumber(),
                customer.getHasSalaryAccount(),
                customer.getTotalAccounts(),
                address,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testCreateNewCustomer_Success() {
        // Stub mappers and repository so that a valid customer is returned.
        when(addressMapper.toAddress(createCustomerRequest.address())).thenReturn(address);
        when(customerMapper.toCustomer(createCustomerRequest)).thenReturn(customer);
        when(customerRepository.findByLegalId("1234567890")).thenReturn(Optional.empty());
        when(customerRepository.findByMobileNumber("0123456789")).thenReturn(Optional.empty());
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toCustomerView(customer)).thenReturn(customerResponse);

        CustomerResponse response = customerService.createNewCustomer(createCustomerRequest);
        assertNotNull(response);
        assertEquals(1234567L, response.id());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testCreateNewCustomer_DuplicateLegalId() {
        when(addressMapper.toAddress(createCustomerRequest.address())).thenReturn(address);
        when(customerMapper.toCustomer(createCustomerRequest)).thenReturn(customer);

        when(customerRepository.findByLegalId("1234567890")).thenReturn(Optional.of(customer));

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> customerService.createNewCustomer(createCustomerRequest));
        assertEquals("Legal ID already exists", ex.getMessage());
    }

    @Test
    void testCreateNewCustomer_DuplicateMobileNumber() {
        when(addressMapper.toAddress(createCustomerRequest.address())).thenReturn(address);
        when(customerMapper.toCustomer(createCustomerRequest)).thenReturn(customer);

        when(customerRepository.findByLegalId("1234567890")).thenReturn(Optional.empty());
        when(customerRepository.findByMobileNumber("0123456789")).thenReturn(Optional.of(customer));

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> customerService.createNewCustomer(createCustomerRequest));
        assertEquals("Mobile number already exists", ex.getMessage());
    }

    @Test
    void testGetAllCustomers_Success() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toCustomerView(customer)).thenReturn(customerResponse);

        List<CustomerResponse> responses = customerService.getAllCustomers();
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testGetAllCustomers_NoCustomersFound() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> customerService.getAllCustomers());
        assertEquals("No customers found", ex.getMessage());
    }

    @Test
    void testSearchCustomer_ById_Success() {
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        when(customerMapper.toCustomerView(customer)).thenReturn(customerResponse);

        Object result = customerService.searchCustomer("id", "1234567");
        assertTrue(result instanceof CustomerResponse);
        CustomerResponse res = (CustomerResponse) result;
        assertEquals(customer.getId(), res.id());
    }

    @Test
    void testSearchCustomer_ByLegalId_Success() {
        when(customerRepository.findByLegalId("1234567890")).thenReturn(Optional.of(customer));
        when(customerMapper.toCustomerView(customer)).thenReturn(customerResponse);

        Object result = customerService.searchCustomer("legalid", "1234567890");
        assertTrue(result instanceof CustomerResponse);
    }

    @Test
    void testSearchCustomer_ByName_Success() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findByNameContainingIgnoreCase("Test")).thenReturn(customers);
        when(customerMapper.toCustomerView(customer)).thenReturn(customerResponse);

        Object result = customerService.searchCustomer("name", "Test");
        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertEquals(1, list.size());
    }

    @Test
    void testSearchCustomer_InvalidCriteria() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> customerService.searchCustomer("invalid", "value"));
        assertEquals("Invalid search criteria.", ex.getMessage());
    }

    @Test
    void testUpdateCustomer_Success() {
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(CustomerType.CORPORATE, "0987654321");
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        CustomerResponse updatedResponse = new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getLegalId(),
                CustomerType.CORPORATE,
                "0987654321",
                customer.getHasSalaryAccount(),
                customer.getTotalAccounts(),
                address,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(customerMapper.toCustomerView(customer)).thenReturn(updatedResponse);

        CustomerResponse response = customerService.updateCustomer(1234567L, updateRequest);
        assertNotNull(response);
        assertEquals(CustomerType.CORPORATE, response.customerType());
        assertEquals("0987654321", response.mobileNumber());
    }

    @Test
    void testUpdateCustomerAddress_Success() {
        // Set an initial address in the customer.
        customer.setAddress(address);
        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest("Office", "NewCountry", "NewCity", "NewStreet", 20);
        // Simulate that when the repository saves the customer, the changes have been persisted.
        when(customerRepository.findById(1234567L)).thenReturn(Optional.of(customer));
        doAnswer(invocation -> {
            // Update the customer's address in-place
            Address addr = customer.getAddress();
            addr.setLabel(updateAddressRequest.label());
            addr.setCountry(updateAddressRequest.country());
            addr.setCity(updateAddressRequest.city());
            addr.setStreet(updateAddressRequest.street());
            addr.setBuildingNumber(updateAddressRequest.buildingNumber());
            return null;
        }).when(customerRepository).save(customer);
        Address result = customerService.updateCustomerAddress(1234567L, updateAddressRequest);
        assertNotNull(result);
        assertEquals("Office", result.getLabel());
        assertEquals("NewCountry", result.getCountry());
        assertEquals("NewCity", result.getCity());
        assertEquals("NewStreet", result.getStreet());
        assertEquals(20, result.getBuildingNumber());
    }
}
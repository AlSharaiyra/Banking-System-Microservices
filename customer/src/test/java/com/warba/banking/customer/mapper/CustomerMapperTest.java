package com.warba.banking.customer.mapper;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.customer.model.enums.CustomerType;
import com.warba.banking.customer.model.mapper.CustomerMapper;
import com.warba.banking.customer.model.request.CreateAddressRequest;
import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
import com.warba.banking.customer.repository.entity.Address;
import com.warba.banking.customer.repository.entity.Customer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerMapperTest {

    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void testToCustomer() {
        // Given a CreateCustomerRequest
        CreateAddressRequest addressRequest = new CreateAddressRequest("Home", "Country", "City", "Street", 10);
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John Doe", "9876543210", CustomerType.RETAIL, "0123456789", addressRequest
        );

        // When mapping to Customer
        Customer customer = customerMapper.toCustomer(request);

        // Then the simple fields should map correctly.
        assertNotNull(customer);
        assertEquals("John Doe", customer.getName());
        assertEquals("9876543210", customer.getLegalId());
        assertEquals(CustomerType.RETAIL, customer.getCustomerType());
        assertEquals("0123456789", customer.getMobileNumber());
        // Fields ignored by the mapper should be null.
        assertNull(customer.getId());
        assertNull(customer.getAddress());
        // registrationDate and lastModified are ignored.
        assertNull(customer.getRegistrationDate());
        assertNull(customer.getLastModified());
    }

    @Test
    void testToCustomerView() {
        // Prepare a Customer with full details.
        Customer customer = new Customer();
        customer.setId(1234567L);
        customer.setName("John Doe");
        customer.setLegalId("9876543210");
        customer.setCustomerType(CustomerType.RETAIL);
        customer.setMobileNumber("0123456789");
        Address addr = new Address(1L, "Home", "Country", "City", "Street", 10);
        customer.setAddress(addr);
        customer.setTotalAccounts(0);
        customer.setHasSalaryAccount(false);
        customer.setRegistrationDate(java.time.LocalDateTime.now());
        customer.setLastModified(java.time.LocalDateTime.now());

        // When mapping to CustomerResponse
        CustomerResponse response = customerMapper.toCustomerView(customer);

        // Then all fields should be mapped
        assertNotNull(response);
        assertEquals(customer.getId(), response.id());
        assertEquals(customer.getName(), response.name());
        assertEquals(customer.getLegalId(), response.legalId());
        assertEquals(customer.getCustomerType(), response.customerType());
        assertEquals(customer.getMobileNumber(), response.mobileNumber());
        assertEquals(customer.getTotalAccounts(), response.totalAccounts());
        // Address should be mapped as well.
        assertNotNull(response.address());
        assertEquals(addr.getLabel(), response.address().getLabel());
    }

    @Test
    void testCustomerDetailsDTO() {
        // Prepare a Customer with specific details.
        Customer customer = new Customer();
        customer.setHasSalaryAccount(true);
        customer.setTotalAccounts(5);

        // When mapping to CustomerDetailsDTO
        CustomerDetailsDTO dto = customerMapper.customerDetailsDTO(customer);

        // Then the DTO should have the expected values.
        assertNotNull(dto);
        assertTrue(dto.hasSalaryAccount());
        assertEquals(5, dto.totalAccounts());
    }
}

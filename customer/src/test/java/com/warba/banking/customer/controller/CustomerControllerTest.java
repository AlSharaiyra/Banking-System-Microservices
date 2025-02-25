package com.warba.banking.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warba.banking.customer.model.enums.CustomerType;
import com.warba.banking.customer.model.request.CreateAddressRequest;
import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.request.UpdateAddressRequest;
import com.warba.banking.customer.model.request.UpdateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
import com.warba.banking.customer.repository.entity.Address;
import com.warba.banking.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private CreateCustomerRequest createCustomerRequest;
    private CustomerResponse customerResponse;
    private Address address;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        address = new Address(1L, "Home", "Country", "City", "Street", 10);
        createCustomerRequest = new CreateCustomerRequest(
                "Test Customer",
                "1234567890",
                CustomerType.RETAIL,
                "0123456789",
                new CreateAddressRequest("Home", "Country", "City", "Street", 10)
        );
        customerResponse = new CustomerResponse(
                1234567L,
                "Test Customer",
                "1234567890",
                CustomerType.RETAIL,
                "0123456789",
                false,
                0,
                address,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testRegisterCustomer() throws Exception {
        when(customerService.createNewCustomer(any(CreateCustomerRequest.class))).thenReturn(customerResponse);
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1234567)))
                .andExpect(jsonPath("$.name", is("Test Customer")))
                .andExpect(jsonPath("$.legalId", is("1234567890")));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(customerResponse));
        mockMvc.perform(get("/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1234567)))
                .andExpect(jsonPath("$[0].name", is("Test Customer")));
    }

    @Test
    void testSearchCustomers_SingleCustomer() throws Exception {
        when(customerService.searchCustomer(eq("id"), eq("1234567"))).thenReturn(customerResponse);
        mockMvc.perform(get("/customers/search")
                        .param("criteria", "id")
                        .param("value", "1234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1234567)))
                .andExpect(jsonPath("$.name", is("Test Customer")));
    }

    @Test
    void testSearchCustomers_MultipleCustomers() throws Exception {
        when(customerService.searchCustomer(eq("name"), eq("Test"))).thenReturn(List.of(customerResponse));
        mockMvc.perform(get("/customers/search")
                        .param("criteria", "name")
                        .param("value", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1234567)));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(CustomerType.CORPORATE, "0987654321");
        CustomerResponse updatedResponse = new CustomerResponse(
                1234567L,
                "Test Customer",
                "1234567890",
                CustomerType.CORPORATE,
                "0987654321",
                false,
                0,
                address,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(customerService.updateCustomer(eq(1234567L), any(UpdateCustomerRequest.class))).thenReturn(updatedResponse);
        mockMvc.perform(put("/customers/{id}", 1234567)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerType", is("CORPORATE")))
                .andExpect(jsonPath("$.mobileNumber", is("0987654321")));
    }

    @Test
    void testUpdateCustomerAddress() throws Exception {
        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest("Office", "NewCountry", "NewCity", "NewStreet", 20);
        Address updatedAddress = new Address(1L, "Office", "NewCountry", "NewCity", "NewStreet", 20);
        when(customerService.updateCustomerAddress(eq(1234567L), any(UpdateAddressRequest.class))).thenReturn(updatedAddress);
        mockMvc.perform(put("/customers/{id}/address", 1234567)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAddressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Office")))
                .andExpect(jsonPath("$.country", is("NewCountry")))
                .andExpect(jsonPath("$.city", is("NewCity")))
                .andExpect(jsonPath("$.street", is("NewStreet")))
                .andExpect(jsonPath("$.buildingNumber", is(20)));
    }
}

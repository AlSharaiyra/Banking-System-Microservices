package com.warba.banking.customer.controller;

import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.request.UpdateAddressRequest;
import com.warba.banking.customer.model.request.UpdateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
import com.warba.banking.customer.repository.entity.Address;
import com.warba.banking.customer.service.CustomerService;
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
@Tag(name = "Customers",
        description = "A set of APIs to perform management and operations on bank customers and their related data.")
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(description = """
            An API call to register a new bank customer.
            """)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<CustomerResponse> register(@RequestBody @Valid CreateCustomerRequest request) {
        log.debug("Creating customer with request: {}", request);

        CustomerResponse response = customerService.createNewCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(description = """
            An API call to retrieve all customers.
            """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CustomerResponse> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @Operation(description = """
            An API call to search for customers by their id, customerId, legalId, or name.
            Searching criteria and search value are passed using request params.
            """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public ResponseEntity<?> searchCustomers(@RequestParam String criteria, @RequestParam String value) {
        Object result = customerService.searchCustomer(criteria, value);

        if (result instanceof List) {
            return ResponseEntity.ok(result);  // Return a list for name search
        } else if (result instanceof CustomerResponse) {
            return ResponseEntity.ok(result);  // Return a single customer for id, customerId, legalId
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(description = """
            An API call to update an existing customer data.
            """)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @RequestBody UpdateCustomerRequest request){
        log.debug("Updating customer with customer ID: {}", id);

        return customerService.updateCustomer(id, request);
    }

    @Operation(description = """
            An API call to update an existing customer address.
            """)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/address")
    public Address updateCustomerAddress(@PathVariable Long id, @RequestBody UpdateAddressRequest request){
        log.debug("Updating customer address with customer ID: {}", id);

        return customerService.updateCustomerAddress(id, request);
    }

}

package com.warba.banking.customer.service;

import com.warba.banking.common.exceptions.ResourceNotFoundException;
import com.warba.banking.customer.infra.exception.DuplicateResourceException;
import com.warba.banking.customer.model.mapper.AddressMapper;
import com.warba.banking.customer.model.mapper.CustomerMapper;
import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.request.UpdateAddressRequest;
import com.warba.banking.customer.model.request.UpdateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
import com.warba.banking.customer.repository.entity.Address;
import com.warba.banking.customer.repository.entity.Customer;
import com.warba.banking.customer.repository.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;

    /**
     * This method is responsible for creating a new customer.
     *
     * @param request data to create a new customer.
     * @return CustomerResponse of created customer.
     */
    @Transactional
    public CustomerResponse createNewCustomer(CreateCustomerRequest request) {
        Address address = addressMapper.toAddress(request.address());

        Customer customer = customerMapper.toCustomer(request);
        customer.setAddress(address);

        // Set the total number of accounts to 0
        customer.setTotalAccounts(0);

        // Set the has_salary_account to false
        customer.setHasSalaryAccount(false);

        if (customerRepository.findByLegalId(request.legalId()).isPresent())
            throw new DuplicateResourceException("Legal ID already exists");

        if (customerRepository.findByMobileNumber(request.mobileNumber()).isPresent())
            throw new DuplicateResourceException("Mobile number already exists");

        customerRepository.save(customer);
        log.info("Customer created with customer ID: {}", customer.getId());

        return customerMapper.toCustomerView(customer);
    }

    /**
     * This method is responsible for retrieving all customers.
     *
     * @return CustomerResponse of created customer.
     */
    public List<CustomerResponse> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty())
            throw new ResourceNotFoundException("No customers found");

        return customers.stream().map(customerMapper::toCustomerView).collect(Collectors.toList());
    }

    /**
     * A Unified Searching method
     * This method is responsible for searching for customers using id, legalId, and name.
     *
     * @param criteria of searching.
     * @param value to search about.
     *
     * @return Object , the search results.
     */
    public Object searchCustomer(String criteria, String value) {
        log.debug("Searching for customer by: {}, with value:{}", criteria, value);

        return switch (criteria.toLowerCase()) {
            case "id" -> customerRepository.findById(Long.valueOf(value)).map(customerMapper::toCustomerView)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + value));

//            case "customerid" -> customerRepository.findByCustomerId(value).map(customerMapper::toCustomerView)
//                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with customer ID: " + value));

            case "legalid" -> customerRepository.findByLegalId(value).map(customerMapper::toCustomerView)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with legal ID: " + value));

            case "name" -> customerRepository.findByNameContainingIgnoreCase(value).stream()
                    .map(customerMapper::toCustomerView).collect(Collectors.toList());

            default -> throw new IllegalArgumentException("Invalid search criteria.");
        };
    }

    /**
     * This method is responsible for updating existing customer data.
     *
     * @param id of customer to be updated.
     * @param request update an existing customer.
     *
     * @return CustomerResponse of updated customer.
     */
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with customer ID: " + id));

        Optional.ofNullable(request.customerType())
                .ifPresent(customer::setCustomerType);

        Optional.ofNullable(request.mobileNumber())
                .filter(mobile -> !mobile.isBlank())
                .ifPresent(customer::setMobileNumber);

        customerRepository.save(customer);
        log.info("Customer updated successfully.");

        return customerMapper.toCustomerView(customer);
    }

    /**
     * This method is responsible for updating existing customer address.
     *
     * @param id of customer`s address to be updated.
     * @param request update an existing customer address.
     *
     * @return Address of updated address.
     */
    @Transactional
    public Address updateCustomerAddress(Long id, UpdateAddressRequest request){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with customer ID: " + id));

        Address address = customer.getAddress();

        Optional.ofNullable(request.label())
                .filter(label -> !label.isBlank())
                .ifPresent(address::setLabel);

        Optional.ofNullable(request.country())
                .filter(country -> !country.isBlank())
                .ifPresent(address::setCountry);

        Optional.ofNullable(request.city())
                .filter(city -> !city.isBlank())
                .ifPresent(address::setCity);

        Optional.ofNullable(request.street())
                .filter(street -> !street.isBlank())
                .ifPresent(address::setStreet);

        Optional.ofNullable(request.buildingNumber())
                .ifPresent(address::setBuildingNumber);

        customer.setAddress(address);

        customerRepository.save(customer);
        log.info("Customer address updated successfully.");

        return address;
    }
}

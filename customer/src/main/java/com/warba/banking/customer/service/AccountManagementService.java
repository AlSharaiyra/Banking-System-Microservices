package com.warba.banking.customer.service;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.common.exceptions.ResourceNotFoundException;
import com.warba.banking.customer.model.mapper.CustomerMapper;
import com.warba.banking.customer.repository.entity.Customer;
import com.warba.banking.customer.repository.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class AccountManagementService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * This method is responsible for retrieving customer details.
     *
     * @param customerId of the required customer details.
     * @return CustomerDetailsDTO contains hasSalaryAccount and totalAccounts
     */
    public CustomerDetailsDTO getCustomerDetails(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        return customerMapper.customerDetailsDTO(customer);
    }


    /**
     * This method is responsible for updating the customer after creating a new account.
     *
     * @param customerId of customer to retrieve the number of associated accounts.
     * @param request    of update customer request.
     */
    public void updateCustomer(Long customerId, CustomerDetailsDTO request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        customer.setHasSalaryAccount(request.hasSalaryAccount());
        customer.setTotalAccounts(request.totalAccounts());

        customerRepository.save(customer);
        log.info("Updated customer with ID: {}", customerId);
    }

    /**
     * This method is responsible for updating the customer after closing an account.
     *
     * @param customerId of customer to be updated.
     * @param isSalaryAccount to check if the closed account is salary account or not.
     */
    public void handleAccountClosed(Long customerId, Boolean isSalaryAccount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        if (isSalaryAccount)
            customer.setHasSalaryAccount(false);

        customer.setTotalAccounts(customer.getTotalAccounts() - 1);

        customerRepository.save(customer);
        log.info("Updated customer with ID: {}", customerId);
    }
}

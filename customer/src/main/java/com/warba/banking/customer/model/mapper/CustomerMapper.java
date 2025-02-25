package com.warba.banking.customer.model.mapper;

import com.warba.banking.common.dto.CustomerDetailsDTO;
import com.warba.banking.customer.model.request.CreateCustomerRequest;
import com.warba.banking.customer.model.response.CustomerResponse;
import com.warba.banking.customer.repository.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "address", ignore = true)
    public abstract Customer toCustomer(CreateCustomerRequest request);

    public abstract CustomerResponse toCustomerView(Customer customer);

    public abstract CustomerDetailsDTO customerDetailsDTO(Customer customer);
}

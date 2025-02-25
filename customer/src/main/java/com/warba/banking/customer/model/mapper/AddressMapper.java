package com.warba.banking.customer.model.mapper;

import com.warba.banking.customer.model.request.CreateAddressRequest;
import com.warba.banking.customer.repository.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {
    @Mapping(target = "id", ignore = true)
    public abstract Address toAddress(CreateAddressRequest request);
}

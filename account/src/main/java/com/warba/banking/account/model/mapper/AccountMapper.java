package com.warba.banking.account.model.mapper;

import com.warba.banking.account.model.request.CreateAccountRequest;
import com.warba.banking.account.model.response.AccountResponse;
import com.warba.banking.account.repository.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "accountStatus", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract Account toAccount(CreateAccountRequest request);

    public abstract AccountResponse toAccountView(Account account);
}

package com.warba.banking.customer.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.customer.model.enums.CustomerType;
import com.warba.banking.customer.repository.entity.Address;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record CustomerResponse(
        @JsonProperty Long id,
        @JsonProperty String name,
        @JsonProperty String legalId,
        @JsonProperty CustomerType customerType,
        @JsonProperty String mobileNumber,
        @JsonProperty Boolean hasSalaryAccount,
        @JsonProperty Integer totalAccounts,
        @JsonProperty Address address,
        @JsonProperty
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime registrationDate,
        @JsonProperty
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModified
) {
}

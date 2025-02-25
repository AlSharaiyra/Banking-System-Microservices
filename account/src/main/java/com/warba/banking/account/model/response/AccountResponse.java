package com.warba.banking.account.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.account.model.enums.AccountStatus;
import com.warba.banking.account.model.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record AccountResponse(
        @JsonProperty Long accountNumber,
        @JsonProperty Long customerId,
        @JsonProperty BigDecimal balance,
        @JsonProperty AccountType accountType,
        @JsonProperty AccountStatus accountStatus,
        @JsonProperty
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime registrationDate,
        @JsonProperty
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModified
) {
}

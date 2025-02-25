package com.warba.banking.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerDetailsDTO(
        @JsonProperty @NotNull
        Boolean hasSalaryAccount,
        @JsonProperty @NotNull
        Integer totalAccounts

) {
}

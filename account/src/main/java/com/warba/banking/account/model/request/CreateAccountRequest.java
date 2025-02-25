package com.warba.banking.account.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.account.model.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateAccountRequest(
        @JsonProperty @Schema(example = "0.00")
        @NotNull BigDecimal balance,
        @JsonProperty @Schema(example = "SAVING")
        @NotNull AccountType accountType
) {
}

package com.warba.banking.customer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.customer.model.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateCustomerRequest(
        @JsonProperty("name") @Schema(example = "Walid Sharaiyra")
        @NotBlank String name,
        @JsonProperty("legalId") @Schema(example = "1234567890")
        @NotBlank String legalId,
        @JsonProperty("customerType") @Schema(example = "RETAIL")
        @NotNull CustomerType customerType,
        @JsonProperty("mobileNumber") @Schema(example = "00962790451531")
        @NotBlank String mobileNumber,
        @JsonProperty("address")
        @NotNull @Valid CreateAddressRequest address
) {
}

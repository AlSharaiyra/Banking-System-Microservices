package com.warba.banking.customer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.customer.model.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateCustomerRequest(
        @JsonProperty("customerType") @Schema(example = "RETAIL")
        CustomerType customerType,
        @JsonProperty("mobileNumber") @Schema(example = "00962790451531")
        String mobileNumber
) {
}

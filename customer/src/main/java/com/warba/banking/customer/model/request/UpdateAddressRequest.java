package com.warba.banking.customer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateAddressRequest(
        @JsonProperty("label") @Schema(example = "Home")
        String label,
        @JsonProperty("country") @Schema(example = "Jordan")
        String country,
        @JsonProperty("city") @Schema(example = "Amman")
        String city,
        @JsonProperty("street") @Schema(example = "Al-Rainbow Street")
        String street,
        @JsonProperty("buildingNumber") @Schema(example = "13")
        Integer buildingNumber
) {
}

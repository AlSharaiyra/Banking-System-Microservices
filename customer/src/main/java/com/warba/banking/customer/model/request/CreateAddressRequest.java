package com.warba.banking.customer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateAddressRequest(
        @JsonProperty("label") @Schema(example = "Home")
        @NotBlank String label,
        @JsonProperty("country") @Schema(example = "Jordan")
        @NotBlank String country,
        @JsonProperty("city") @Schema(example = "Amman")
        @NotBlank String city,
        @JsonProperty("street") @Schema(example = "Al-Rainbow Street")
        @NotBlank String street,
        @JsonProperty("buildingNumber") @Schema(example = "13")
        Integer buildingNumber
) {
}

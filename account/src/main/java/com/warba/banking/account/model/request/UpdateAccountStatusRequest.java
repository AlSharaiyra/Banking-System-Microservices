package com.warba.banking.account.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.warba.banking.account.model.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateAccountStatusRequest(
        @JsonProperty @Schema(example = "SUSPENDED")
        @NotNull AccountStatus accountStatus
        ) {
}

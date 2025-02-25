package com.warba.banking.customer.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Retry limit exceeded")
public class RetryLimitExceededException extends RuntimeException {
    public RetryLimitExceededException(String message) {
        super(message);
    }
}

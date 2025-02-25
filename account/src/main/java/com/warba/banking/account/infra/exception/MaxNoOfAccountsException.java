package com.warba.banking.account.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MaxNoOfAccountsException extends BusinessLogicException {
    public MaxNoOfAccountsException(String message) {
        super(message);
    }
}

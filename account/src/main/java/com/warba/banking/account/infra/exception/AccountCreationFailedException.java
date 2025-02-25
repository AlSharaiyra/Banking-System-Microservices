package com.warba.banking.account.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AccountCreationFailedException extends RuntimeException{
    public AccountCreationFailedException(String message){
        super(message);
    }
}

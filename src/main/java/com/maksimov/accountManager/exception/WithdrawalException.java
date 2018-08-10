package com.maksimov.accountManager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Not enough money")
public class WithdrawalException extends RuntimeException {
    public WithdrawalException() {
        super();
    }

    public WithdrawalException(String message) {
        super(message);
    }
}

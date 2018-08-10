package com.maksimov.accountManager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Negative number is received")
public class NegativeNumberException extends RuntimeException {
}

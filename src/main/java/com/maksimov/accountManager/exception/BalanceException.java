package com.maksimov.accountManager.exception;

public class BalanceException extends ExceptionHandler {
    public final String BALANCE_ERROR = "Error with balance";

    public BalanceException(String message) {
        super("Error with balance" + ": " + message);
    }
}

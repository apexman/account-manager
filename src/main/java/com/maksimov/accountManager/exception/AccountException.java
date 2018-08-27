package com.maksimov.accountManager.exception;

public class AccountException extends RuntimeException {
    public AccountException() {
    }

    public AccountException(String s) {
        super(s);
    }

    public AccountException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AccountException(Throwable throwable) {
        super(throwable);
    }

    public AccountException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}

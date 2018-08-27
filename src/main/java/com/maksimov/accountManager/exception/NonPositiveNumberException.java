package com.maksimov.accountManager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class NonPositiveNumberException extends AccountException {
    private final Logger logger = LoggerFactory.getLogger(NonPositiveNumberException.class);

    public NonPositiveNumberException(BigDecimal number) {
        super(String.format("Number must be a non-negative: %s", number));
        logger.error(String.format("Number must be a non-negative: %s", number));
    }
}

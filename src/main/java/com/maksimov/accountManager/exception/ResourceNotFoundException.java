package com.maksimov.accountManager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceNotFoundException extends AccountException {
    private final Logger logger = LoggerFactory.getLogger(NotEnoughMoneyException.class);

    public ResourceNotFoundException(String id) {
        super(String.format("There is no resource with id %s", id));
        logger.error(String.format("There is no resource with id %s", id));
    }
}

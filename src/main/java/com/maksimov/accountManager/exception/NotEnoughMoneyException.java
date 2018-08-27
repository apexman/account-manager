package com.maksimov.accountManager.exception;

import com.maksimov.accountManager.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotEnoughMoneyException extends AccountException {
    private final Logger logger = LoggerFactory.getLogger(NotEnoughMoneyException.class);

    public NotEnoughMoneyException(Account account) {
        super(
                String.format(
                "There is not enough money to withdraw from account %s",
                account.getId())
        );

        logger.error(
                String.format(
                "There is not enough money to withdraw from account %s",
                account.getId())
        );
    }
}

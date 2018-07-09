package com.maksimov.moneyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InitTestData {
    Logger logger = LoggerFactory.getLogger(InitTestData.class);

    @Autowired
    AccountRepository accountRepositoryy;

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData(){
        logger.info("Initialize test data");

        Account account = new Account();
        account.setId("165d4252b8f645f0b66c1fc7f727bb4a");
        account.setBalance(BigDecimal.ONE);
        System.out.println(account);
        accountRepositoryy.save(account);

        logger.info("Initialization completed");
    }

}
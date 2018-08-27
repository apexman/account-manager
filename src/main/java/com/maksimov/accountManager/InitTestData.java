package com.maksimov.accountManager;

import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.repository.IAccountRepository;
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
    IAccountRepository accountRepositoryy;

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        logger.info("Initialize test data");
        createAccount("165d4252b8f645f0b66c1fc7f727bb4a", BigDecimal.valueOf(100), "Test account 1");
        createAccount("0b66c1fc7f727bb4a165d4252b8f645f", BigDecimal.valueOf(100), "Test account 2");
        logger.info("Initialization completed");
    }

    private void createAccount(String id, BigDecimal money, String name) {
        Account account = new Account();
        account.setId(id);
        account.setBalance(money);
        account.setName(name);
        System.out.println(account);
        accountRepositoryy.save(account);

    }

}
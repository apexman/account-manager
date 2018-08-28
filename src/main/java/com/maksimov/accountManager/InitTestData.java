package com.maksimov.accountManager;

import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.model.Client;
import com.maksimov.accountManager.repository.AccountRepository;
import com.maksimov.accountManager.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InitTestData {
    private Logger logger = LoggerFactory.getLogger(InitTestData.class);
    private AccountRepository accountRepository;
    private ClientRepository clientRepository;

    @Autowired
    public InitTestData(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initTestData() {
        logger.info("Initialize test data");
        Client client = createClient("alex", "12345");

        createAccount("165d4252b8f645f0b66c1fc7f727bb4a", BigDecimal.valueOf(100), "Test account 1", client);
        createAccount("0b66c1fc7f727bb4a165d4252b8f645f", BigDecimal.valueOf(100), "Test account 2", client);
        createAccount("TODELETE", BigDecimal.valueOf(100), "Test account 3", client);

        logger.info("Initialization completed");
    }

    private Client createClient(String login, String password) {
        return clientRepository.save(new Client(login, password));
    }

    private void createAccount(String id, BigDecimal money, String name, Client client) {
        Account account = new Account(name, money, client);
        account.setId(id);
        logger.info(account.toString());
        accountRepository.save(account);
    }
}
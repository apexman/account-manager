package com.maksimov.accountManager.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    Logger logger = LoggerFactory.getLogger(AccountController.class);
    public static final String HELLO_TEXT = "hello from account controller";

    @Autowired
    AccountRepository accountRepository;

    @RequestMapping(value = "/hello")
    public String sayHello(){
        return HELLO_TEXT;
    }

    @RequestMapping(value = {"", "/", "all"}, method = RequestMethod.GET)
    public Iterable<Account> getAll() {
        logger.info("getAll");
        return accountRepository.findAll();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Account update(Account account) {
        logger.info("update account " + account);

        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            accountRepository.save(account);

        return account;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(Account account) {
        logger.info("delete account: " + account);

        accountRepository.deleteById(account.getId());
        return "Deleted";
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public Optional<Account> deposit(Account account, BigDecimal deposit){
        Optional<Account> updatedAcc = accountRepository.deposit(account.getId(), deposit);
        return updatedAcc;
    }
}

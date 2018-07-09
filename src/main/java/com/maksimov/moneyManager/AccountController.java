package com.maksimov.moneyManager;

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

    @Autowired
    AccountRepository accountRepository;

    @RequestMapping(value = {"", "/", "all"}, method = RequestMethod.GET)
    public Iterable<Account> getAll() {
        logger.info("getAll");
        return accountRepository.findAll();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(Account account) {
        if (!accountRepository.existsById(account.getId())) {
            accountRepository.save(account);
            return "Added";
        }
        return "Already exists";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(Account account) {
        accountRepository.delete(account);
        return "Deleted";
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public Optional<Account> deposit(Account account, BigDecimal money) {
        logger.info("deposit: " + account + " money " + money);
//        accountRepository.deposit(account.getId(), money);
        return accountRepository.findById(account.getId());
    }

    @RequestMapping(value = "/withdraw ", method = RequestMethod.POST)
    public Optional<Account> withdraw(Account account, BigDecimal money) {
        logger.info("withdraw: " + account + " money " + money);
        accountRepository.withdraw(account.getId(), money);
        return accountRepository.findById(account.getId());
    }
}

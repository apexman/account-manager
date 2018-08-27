package com.maksimov.accountManager.controller;

import com.maksimov.accountManager.dto.AccountTO;
import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.service.AccountService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);
    public static final String HELLO_TEXT = "hello from account controller";

    private AccountService accountService;
    private ModelMapper mapper;

    @Autowired
    public AccountController(AccountService accountService, ModelMapper mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @RequestMapping(value = "/hello")
    public String sayHello() {
        return HELLO_TEXT;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AccountTO>> getAll() {
        logger.info("getAll");
        List<Account> accounts = accountService.findAll();
        List<AccountTO> accountTOS = accounts.stream()
                .map(account -> mapper.map(account, AccountTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(accountTOS);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<AccountTO> getAccountById(@PathVariable String id) {
        logger.info("Reading account with id " + id + " from database.");
        Account account = accountService.findById(id);
        return ResponseEntity.ok().body(mapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> update(Account account) {
        logger.info("update account " + account);

        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            accountService.save(account);
        }

        return ResponseEntity.ok().body(mapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        logger.info("delete account: " + id);

        accountService.deleteById(id);
    }

    @RequestMapping(value = "/deposit/{id}", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> deposit(@PathVariable String id, @RequestParam BigDecimal deposit) {
        logger.info("deposit " + deposit);

        accountService.deposit(id, deposit);
        Account account = accountService.findById(id);
        return ResponseEntity.ok().body(mapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/withdraw/{id}", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> withdraw(@PathVariable String id, @RequestParam BigDecimal withdrawn) {
        logger.info("withdraw " + withdrawn);

        accountService.withdraw(id, withdrawn);
        Account account = accountService.findById(id);
        return ResponseEntity.ok().body(mapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public void transfer(@RequestParam String idFrom, @RequestParam String idWhere, @RequestParam BigDecimal money) {
        accountService.transfer(idFrom, idWhere, money);
    }
}
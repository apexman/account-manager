package com.maksimov.accountManager.controller;

import com.maksimov.accountManager.dto.AccountTO;
import com.maksimov.accountManager.dto.AccountTransactionTO;
import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.model.AccountTransaction;
import com.maksimov.accountManager.service.AccountService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);
    public static final String HELLO_TEXT = "hello from account controller";

    private AccountService accountService;
    private ModelMapper modelMapper;

    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "/hello")
    public String sayHello() {
        return HELLO_TEXT;
    }

    @RequestMapping(value = {"", "/", "all"}, method = RequestMethod.GET)
    public ResponseEntity<List<AccountTO>> getAll() {
        logger.info("getAll");
        List<AccountTO> accounts = StreamSupport.stream(accountService.findAll().spliterator(), false)
                .map(account -> modelMapper.map(account, AccountTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(accounts);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<AccountTO> getAccount(@PathVariable String id) {
        logger.info("Reading account with id " + id + " from database.");
        Account account = accountService.findById(id);
        return ResponseEntity.ok().body(modelMapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> save(Account account) {
        logger.info("save account " + account);
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            accountService.save(account);
        }
        return ResponseEntity.ok().body(modelMapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        logger.info("delete account: " + id);
        accountService.deleteById(id);
    }

    @RequestMapping(value = "/deposit/{id}", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> deposit(@PathVariable String id, @RequestParam BigDecimal deposit) {
        logger.info("deposit " + deposit);
        Account account = accountService.deposit(id, deposit);
        return ResponseEntity.ok().body(modelMapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/withdraw/{id}", method = RequestMethod.POST)
    public ResponseEntity<AccountTO> withdraw(@PathVariable String id, @RequestParam BigDecimal withdrawn) {
        logger.info("withdraw " + withdrawn);
        Account account = accountService.withdraw(id, withdrawn);
        return ResponseEntity.ok().body(modelMapper.map(account, AccountTO.class));
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<AccountTransactionTO> transfer(@RequestParam String idFrom, @RequestParam String idWhere, @RequestParam BigDecimal money) {
        AccountTransaction transfer = accountService.transfer(idFrom, idWhere, money);
        logger.info(transfer.toString());
        return ResponseEntity.ok().body(modelMapper.map(transfer, AccountTransactionTO.class));
    }
}

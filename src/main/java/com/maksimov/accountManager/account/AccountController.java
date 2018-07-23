package com.maksimov.accountManager.account;

import com.maksimov.accountManager.exception.BalanceException;
import com.maksimov.accountManager.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);
    public static final String HELLO_TEXT = "hello from account controller";

    @Autowired
    private AccountService accountService;// = new AccountRepository();

    @RequestMapping(value = "/hello")
    public String sayHello(){
        return HELLO_TEXT;
    }

    @RequestMapping(value = {"", "/", "all"}, method = RequestMethod.GET)
    public Iterable<Account> getAll() {
        logger.info("getAll");
        return accountService.findAll();
    }

    @RequestMapping(value="/{id}")
    public Account getAccountById(@PathVariable String id) {
        logger.info("Reading account with id " + id + " from database.");
        return accountService.findById(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Account update(Account account) {
        logger.info("update account " + account);

        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            try {
                accountService.save(account);
            } catch (BalanceException e) {
                logger.error(e.getMessage());
                //надо сообщить об этом както
            }
        }

        return account;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@PathVariable String id) {
        logger.info("delete account: " + id);

        accountService.deleteById(id);
        return "Deleted";
    }

    @RequestMapping(value = "/deposit/{id}", method = RequestMethod.POST)
    public Account deposit(@PathVariable String id, @RequestParam BigDecimal deposit) {
        logger.info("deposit " + deposit);

        try {
            accountService.deposit(id, deposit);
        } catch (ExceptionHandler exceptionHandler) {
            logger.error(exceptionHandler.getMessage());
            //hz
        }

        return accountService.findById(id);
    }

    @RequestMapping(value = "/withdraw/{id}", method = RequestMethod.POST)
    public Account withdraw(@PathVariable String id, @RequestParam BigDecimal withdrawn) {
        logger.info("withdraw " + withdrawn);

        try {
            accountService.withdraw(id, withdrawn);
        } catch (ExceptionHandler exceptionHandler) {
            logger.error(exceptionHandler.getMessage());
            //hz
        }

        return accountService.findById(id);
    }

    @RequestMapping(value = "/transfer")
    public void transfer(@RequestParam String idFrom, @RequestParam String idWhere, @RequestParam BigDecimal money) {
        try {
            accountService.transfer(idFrom, idWhere, money);
        } catch (ExceptionHandler exceptionHandler) {
            logger.error(exceptionHandler.getMessage());
        }
    }
}

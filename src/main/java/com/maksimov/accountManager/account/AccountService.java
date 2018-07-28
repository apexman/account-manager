package com.maksimov.accountManager.account;

import com.maksimov.accountManager.exception.BalanceException;
import com.maksimov.accountManager.exception.ExceptionHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(rollbackOn = Throwable.class)
public class AccountService {
    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private static final String RESOURCE_KEY = "lockService.resources";

    public final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(@NotNull String id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account save(@NotNull Account account) throws BalanceException {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            return accountRepository.save(account);
        else
            throw new BalanceException("Balance must be non-negative: " + account.getBalance());
    }

    public void deleteById(@NotNull String id) {
        accountRepository.deleteById(id);
    }

    public Account deposit(@NotNull String id, @NotNull BigDecimal deposit) throws ExceptionHandler {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(deposit) && doesPresentAccount(account)) {
            account.setBalance(account.getBalance().add(deposit));
            return this.save(account);
        } else
            return null;
    }

    public Account withdraw(@NotNull String id, @NotNull BigDecimal money) throws ExceptionHandler {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(money) && doesPresentAccount(account)) {
            if (account.getBalance().compareTo(money) >= 0) {
                account.setBalance(account.getBalance().subtract(money));
                return this.save(account);
            } else
                throw new BalanceException("Balance is fewer then money withdrawn: money withdrawn = " + money + "; account = " + account);
        } else
            return null;
    }

    public void transfer(@NotNull String accFromId, @NotNull String accWhereId, @NotNull BigDecimal money) throws ExceptionHandler {
        isPositiveNumber(money);
        if (accFromId.compareTo(accWhereId) > 0) {
            accountService.deposit(accWhereId, money);
            accountService.withdraw(accFromId, money);
        } else {
            accountService.withdraw(accFromId, money);
            accountService.deposit(accWhereId, money);
        }
    }

    private boolean isPositiveNumber(@NotNull BigDecimal bigDecimal) throws ExceptionHandler {
        if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
            return true;
        } else
            throw new ExceptionHandler("Number must be a non-negative");
    }

    private boolean doesPresentAccount(Account account) throws ExceptionHandler {
        if (account != null) {
            return true;
        } else
            throw new ExceptionHandler("Account is missing");
    }
}

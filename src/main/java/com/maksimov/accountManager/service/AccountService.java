package com.maksimov.accountManager.service;

import com.maksimov.accountManager.exception.NegativeNumberException;
import com.maksimov.accountManager.exception.ThereIsNoSuchAccountException;
import com.maksimov.accountManager.exception.WithdrawalException;
import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.model.AccountTransaction;
import com.maksimov.accountManager.repository.AccountRepository;
import com.maksimov.accountManager.repository.AccountTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@Transactional(rollbackOn = Throwable.class)
public class AccountService {
    public final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private AccountRepository accountRepository;
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired

    public AccountService(AccountRepository iAccountRepository, AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = iAccountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(String id) {
        Account account = accountRepository.findById(id).orElse(null);
        doesAccountExist(account);
        return account;
    }

    public Account save(Account account) {
        doesAccountExist(account);
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            return accountRepository.save(account);
        else
            throw new NegativeNumberException();
    }

    public void deleteById(String id) {
        Account account = accountRepository.findOneAndLock(id);
        doesAccountExist(account);
        accountRepository.deleteById(id);
    }

    public Account deposit(String id, BigDecimal deposit) {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(deposit) && doesAccountExist(account)) {
            account.setBalance(account.getBalance().add(deposit));

            AccountTransaction transaction = new AccountTransaction(null, account, deposit);
            accountTransactionRepository.save(transaction);

            return this.save(account);
        } else
            return null;
    }

    public Account withdraw(String id, BigDecimal money) {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(money) && doesAccountExist(account)) {
            if (account.getBalance().compareTo(money) >= 0) {
                account.setBalance(account.getBalance().subtract(money));

                AccountTransaction transaction = new AccountTransaction(account, null, money);
                accountTransactionRepository.save(transaction);

                return this.save(account);
            } else
                throw new WithdrawalException("Balance is fewer then money withdrawn: money withdrawn = " + money);
        } else
            return null;
    }

    public AccountTransaction transfer(String accFromId, String accWhereId, BigDecimal money) {
        //ordered locking pattern
        if (accFromId.compareTo(accWhereId) > 0) {
            this.deposit(accWhereId, money);
            this.withdraw(accFromId, money);
        } else {
            this.withdraw(accFromId, money);
            this.deposit(accWhereId, money);
        }

        Account fromAcc = accountRepository.findOneAndLock(accFromId);
        Account whereAcc = accountRepository.findOneAndLock(accWhereId);

        return accountTransactionRepository.save(new AccountTransaction(fromAcc, whereAcc, money));
    }

    private boolean isPositiveNumber(BigDecimal bigDecimal) {
        if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeNumberException();
        }
        return true;
    }

    private boolean doesAccountExist(Account account) {
        if (account == null) {
            throw new ThereIsNoSuchAccountException();
        }
        return true;
    }
}

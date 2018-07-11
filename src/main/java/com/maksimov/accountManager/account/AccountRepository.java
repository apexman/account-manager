package com.maksimov.accountManager.account;

import com.maksimov.accountManager.exception.BalanceException;
import com.maksimov.accountManager.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class AccountRepository {
    @Autowired
    private IAccountRepository accountRepository;

    public Account save(Account account) throws BalanceException {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            return accountRepository.save(account);
        else
            throw new BalanceException("Balance must be non-negative: " + account.getBalance());
    }

    public void deleteById(String id) {
        accountRepository.deleteById(id);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Account deposit(String id, BigDecimal deposit) throws ExceptionHandler {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (isPresentAccountAndNonnegativeNumber(accountOptional, deposit)) {
            Account account = accountOptional.get();
            account.setBalance(account.getBalance().add(deposit));
            return this.save(account);
        } else
            return null;
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Account withdraw(String id, BigDecimal money) throws ExceptionHandler {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (isPresentAccountAndNonnegativeNumber(accountOptional, money)){
            if (accountOptional.get().getBalance().compareTo(money) >= 0) {
                Account account = accountOptional.get();
                account.setBalance(account.getBalance().subtract(money));
                return this.save(account);
            } else
                throw new BalanceException("Balance is fewer then money withdrawn: money withdrawn = " + money + "; account = " + accountOptional.get());
        } else
            return null;
    }

    private boolean isPresentAccountAndNonnegativeNumber(Optional<Account> accountOptional, BigDecimal bigDecimal) throws ExceptionHandler {
        if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
            if (accountOptional.isPresent()) {
                return true;
            } else
                throw new ExceptionHandler("Account is missing");
        } else
            throw new ExceptionHandler("Number must be a non-negative");
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }
}

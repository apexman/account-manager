package com.maksimov.accountManager.service;

import com.maksimov.accountManager.exception.NonPositiveNumberException;
import com.maksimov.accountManager.exception.NotEnoughMoneyException;
import com.maksimov.accountManager.exception.ResourceNotFoundException;
import com.maksimov.accountManager.model.Account;
import com.maksimov.accountManager.repository.AccountRepository;
import org.modelmapper.internal.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(rollbackOn = Throwable.class)
public class AccountService {
    public final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findAll() {
        return Lists.from(accountRepository.findAll().iterator());
    }

    public Account findById(@NotNull String id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account save(@NotNull Account account) throws NonPositiveNumberException {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            return accountRepository.save(account);
        else
            throw new NonPositiveNumberException(account.getBalance());
    }

    public void deleteById(@NotNull String id) {
        accountRepository.deleteById(id);
    }

    public Account deposit(@NotNull String id, @NotNull BigDecimal deposit) throws ResourceNotFoundException, NonPositiveNumberException {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(deposit) && doesPresentAccount(account, id)) {
            account.setBalance(account.getBalance().add(deposit));
            return this.save(account);
        } else
            return null;
    }

    public Account withdraw(@NotNull String id, @NotNull BigDecimal money) throws NotEnoughMoneyException, NonPositiveNumberException, ResourceNotFoundException {
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(money) && doesPresentAccount(account, id)) {
            if (account.getBalance().compareTo(money) >= 0) {
                account.setBalance(account.getBalance().subtract(money));
                return this.save(account);
            } else
                throw new NotEnoughMoneyException(account);
        } else
            return null;
    }

    public void transfer(@NotNull String accFromId, @NotNull String accWhereId, @NotNull BigDecimal money) throws NonPositiveNumberException, NotEnoughMoneyException, ResourceNotFoundException {
        isPositiveNumber(money);
        if (accFromId.compareTo(accWhereId) > 0) {
            this.deposit(accWhereId, money);
            this.withdraw(accFromId, money);
        } else {
            this.withdraw(accFromId, money);
            this.deposit(accWhereId, money);
        }
    }

    private boolean isPositiveNumber(@NotNull BigDecimal bigDecimal) throws NonPositiveNumberException {
        if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
            return true;
        } else
            throw new NonPositiveNumberException(bigDecimal);
    }

    private boolean doesPresentAccount(Account account, String id) throws ResourceNotFoundException {
        if (account != null) {
            return true;
        } else
            throw new ResourceNotFoundException(id);
    }
}

package com.maksimov.accountManager.account;

import com.maksimov.accountManager.exception.BalanceException;
import com.maksimov.accountManager.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
public class AccountService {
    @Autowired
    private IAccountRepository accountRepository;

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(String id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account save(Account account) throws BalanceException {
        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0)
            return accountRepository.save(account);
        else
            throw new BalanceException("Balance must be non-negative: " + account.getBalance());
    }

    public void deleteById(String id) {
        accountRepository.deleteById(id);
    }

    public Account deposit(String id, BigDecimal deposit) throws ExceptionHandler {
        Account account = accountRepository.findOneAndLock(id);

        if (doesPresentAccountAndNonnegativeNumber(account, deposit)) {
            account.setBalance(account.getBalance().add(deposit));
            return this.save(account);
        } else
            return null;
    }

    public Account withdraw(String id, BigDecimal money) throws ExceptionHandler {
        Account account = accountRepository.findOneAndLock(id);

        if (doesPresentAccountAndNonnegativeNumber(account, money)){
            if (account.getBalance().compareTo(money) >= 0) {
                account.setBalance(account.getBalance().subtract(money));
                return this.save(account);
            } else
                throw new BalanceException("Balance is fewer then money withdrawn: money withdrawn = " + money + "; account = " + account);
        } else
            return null;
    }

    public void transfer(String accFrom, String accWhere, BigDecimal money) throws ExceptionHandler {
        System.out.println("STARTED " + money);
//        accountRepository.findTwoAndLock(accFrom, accWhere);
        withdraw(accFrom, money);
        deposit(accWhere, money);
        System.out.println("ENDED " + money);
    }

    private boolean doesPresentAccountAndNonnegativeNumber(Account account, BigDecimal bigDecimal) throws ExceptionHandler {
        if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
            if (account != null) {
                return true;
            } else
                throw new ExceptionHandler("Account is missing");
        } else
            throw new ExceptionHandler("Number must be a non-negative");
    }
}

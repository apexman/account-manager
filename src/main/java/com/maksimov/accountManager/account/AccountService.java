package com.maksimov.accountManager.account;

import com.maksimov.accountManager.exception.BalanceException;
import com.maksimov.accountManager.exception.ExceptionHandler;
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

//    @Autowired
//    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private SessionFactory sessionFactory;

    private static final String RESOURCE_KEY = "lockService.resources";

    public final Logger logger = LoggerFactory.getLogger(AccountService.class);

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
        accountService.lock(id);
        Account account = accountRepository.findOneAndLock(id);

        if (isPositiveNumber(deposit) && doesPresentAccount(account)) {
            account.setBalance(account.getBalance().add(deposit));
            return this.save(account);
        } else
            return null;
    }

    public Account withdraw(String id, BigDecimal money) throws ExceptionHandler {
        accountService.lock(id);
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

    public void transfer(String accFromId, String accWhereId, BigDecimal money) throws ExceptionHandler {
//        System.out.println("STARTED " + money);
        long start = System.currentTimeMillis();

        isPositiveNumber(money);

        accountService.withdraw(accFromId, money);
        accountService.deposit(accWhereId, money);

//        System.out.println("ENDED " + money);
        System.out.println("DELTA: " + (System.currentTimeMillis() - start));
    }

    public void lock(final String id) {
//        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Set<String> locked = (Set<String>) TransactionSynchronizationManager.getResource(RESOURCE_KEY);
        boolean applyLock = false;
        boolean registerListener = false;

        if (locked == null) {
            locked = new HashSet<>();
            TransactionSynchronizationManager.bindResource(RESOURCE_KEY, locked);
            registerListener = true;
        }

        if (!locked.contains(id)) {
            locked.add(id);
            applyLock = true;
        }

        if (applyLock) {
            if (logger.isDebugEnabled())
                logger.debug("Attempting to acquire lock for: {}", id);

            // performs the lock
            try {
                sessionFactory.getCurrentSession().doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        connection.prepareStatement(String.format("select * from customer where id='%s' for update", id)).
                                execute();
                    }
                });
            } catch (LockAcquisitionException e) {
                logger.warn("Failed to acquire lock for: {}, deadlock detected", id);
                locked.remove(id);
                throw e;
            }

            if (logger.isDebugEnabled())
                logger.debug("Lock acquired for: {}", id);

            if (registerListener) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        Set<String> locked = (Set<String>) TransactionSynchronizationManager.getResource(RESOURCE_KEY);

                        if (locked != null) {
                            if (logger.isDebugEnabled())
                                logger.debug("Releasing locks for: {}", locked.toString());

                            TransactionSynchronizationManager.unbindResource(RESOURCE_KEY);
                        }
                    }
                });
            } else {
                if (logger.isTraceEnabled())
                    logger.trace("This is the further lock for this transaction, what might cause deadlock!");
            }

        } else {
            if (logger.isDebugEnabled())
                logger.debug("Lock already acquired for: {}, skipping", id);
        }
    }

    private boolean isPositiveNumber(BigDecimal bigDecimal) throws ExceptionHandler {
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
